///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2000 Davanum Srinivas (dims@geocities.com)
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// You should have received a copy of the GNU General Public License along with
// this program; if not, write to the
// Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
///////////////////////////////////////////////////////////////////////////////

#define _WIN32_WINNT (0x0400)
#include <windows.h>
#include <windowsx.h>
#include <process.h>
#include <search.h>          // for qsort and bsearch
#include "JFrameEx.h"

// JVM independent entry point for getting the current JVM.
extern "C" {
	typedef jint (JNICALL *pJNI_GetCreatedJavaVMs)(JavaVM **, jsize, jsize *);
}

// Thread for notifying the java code.
void WINAPIV StartNotifyThread(LPVOID lpVoid);


// Using arrays structures for callback mappings

// MAINTENANCE NOTE:  T.Noble 10/3/2000
// Removing dependency on STL removed dependency on MSVCP60.DLL, which
// some people might not have (i.e. anybody running NT 4 who hasn't
// installed DevStudio at some point).  Plus it decreases the size of
// the DLL, which is always nice.
//
// This implementation should actually be faster for lookups than using
// the STL map for a couple of reasons:
// 1. We now cache the HWND->Proc and HWND->Frame from the last getProc
//    and getFrame operation, respectively.  So the first wheel motion
//    after a frame gets focus will require a lookup of the HWND mapping,
//    but each subsequent wheel motion within that window uses the cached
//    value.  Ref: "principle of proximity" or "principle of nearness"
// 2. Using qsort each time an array element is added keeps the arrays
//    in order by HWND.  Then using bsearch for the lookup is a true
//    O(log(N)) complexity operation.  Note that this is equivalent to
//    the map lookup in STL, since current STL implementations use
//    Red/Black tree as the backing store (which is also O(log(N)) lookup).
//
// Note that inserts might be slightly slower for large numbers of
// windows, since you incur O(N*log(N)) for the qsort, whereas insertion
// into a Red/Black tree is O(log(N)).  This should be negligible compared
// to the startup time of a JFrame, even if the user does have a few
// thousand windows open already ;->

typedef struct hwnd2object_struct {
   HWND hwnd;
   jobject object;
} HWND2OBJECT;

typedef struct hwnd2wndproc_struct {
   HWND hwnd;
   WNDPROC proc;
} HWND2PROC;

// Hopefully nobody would open more than this many windows at a time
// Good luck doing so with Java and not running into problems!
// If this does become a problem, then maybe make this dynamically
// allocated in the future.
static const int MAX_JWINDOWS=8192;

// Shared Data - access to these is entirely protected by critical sections
static HWND2PROC theProcArray[MAX_JWINDOWS];
static HWND2OBJECT theFrameArray[MAX_JWINDOWS];

static int cntFrame = 0;
static int cntProc = 0;

static HWND cacheFrameHwnd = 0;
static jobject cacheFrameObject = 0;
static HWND cacheProcHwnd = 0;
static WNDPROC cacheProcProc = 0;

// Critical section objects to protect shared data
// Initialized/deleted in DllMain
static CRITICAL_SECTION csProcMap;
static CRITICAL_SECTION csFrameMap;

// DllMain is called once with DLL_PROCESS_ATTACH when the
// DLL is loaded by the process, and once with DLL_PROCESS_DETACH
// when the DLL is unloaded.  Critical sections are shared by
// all threads within only one process, so it's safe to initialize
// and delete the critical section objects here.
BOOLEAN WINAPI DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved)
{
	switch (fdwReason)
	{
		case DLL_PROCESS_ATTACH:
			// Initialize the critical sections
			InitializeCriticalSection(&csProcMap);
			InitializeCriticalSection(&csFrameMap);
			break;
		case DLL_PROCESS_DETACH:
			// Clean up critical sections
			DeleteCriticalSection(&csProcMap);
			DeleteCriticalSection(&csFrameMap);
			break;
	}
   return TRUE;
}

// Compare any two structures whose first field is an HWND
// Used by qsort and bsearch
int __cdecl compareHwnd (const void *elem1, const void *elem2 )
{
	HWND *hwnd1 = (HWND *)elem1;
	HWND *hwnd2 = (HWND *)elem2;
	if (*hwnd1 == *hwnd2) return 0;
	if (*hwnd1 < *hwnd2) return -1;
	return 1;
}

// Helpers for getting and storing the JFrameEx object
void setFrame(HWND hwnd, jobject object)
{
	__try
	{
		EnterCriticalSection(&csFrameMap);

		if (cntFrame < MAX_JWINDOWS)
		{
			theFrameArray[cntFrame].hwnd = hwnd;
			theFrameArray[cntFrame++].object = object;

			qsort(theFrameArray, cntFrame, sizeof(HWND2OBJECT), &compareHwnd);
		}
	}
	__finally { LeaveCriticalSection(&csFrameMap); }
}
jobject getFrame(HWND hwnd)
{
	jobject retVal = 0;
	__try
	{
		EnterCriticalSection(&csFrameMap);

		if (hwnd == cacheFrameHwnd)
			retVal = cacheFrameObject;

		HWND2OBJECT *h2o = (HWND2OBJECT *)bsearch(&hwnd, theFrameArray, cntFrame, sizeof(HWND2OBJECT), &compareHwnd);
		if (h2o != NULL)
		{
			retVal = h2o->object;
			cacheFrameHwnd = hwnd;
			cacheFrameObject = retVal;
		}
	}
	__finally { LeaveCriticalSection(&csFrameMap); }

	return retVal;
}

// Helpers for getting and storing the initial window proc's
void setProc(HWND hwnd, WNDPROC proc)
{
	__try
	{
		EnterCriticalSection(&csProcMap);

		if (cntProc < MAX_JWINDOWS)
		{
			theProcArray[cntProc].hwnd = hwnd;
			theProcArray[cntProc++].proc = proc;

			qsort(theProcArray, cntProc, sizeof(HWND2PROC), &compareHwnd);
		}
	}
	__finally { LeaveCriticalSection(&csProcMap); }
}
WNDPROC getProc(HWND hwnd)
{
	WNDPROC retVal = 0;
	__try
	{
		EnterCriticalSection(&csProcMap);

		if (hwnd == cacheProcHwnd)
			retVal = cacheProcProc;

		HWND2PROC *h2p = (HWND2PROC *)bsearch(&hwnd, theProcArray, cntProc, sizeof(HWND2PROC), &compareHwnd);
		if (h2p != NULL)
		{
			retVal = h2p->proc;
			cacheProcHwnd = hwnd;
			cacheProcProc = retVal;
		}
	}
	__finally { LeaveCriticalSection(&csProcMap); }

	return retVal;
}

// Window Proc for subclassing.
LRESULT CALLBACK FrameWindowProc(
  HWND hwnd,      // handle to window
  UINT uMsg,      // message identifier
  WPARAM wParam,  // first message parameter
  LPARAM lParam   // second message parameter
)
{
	// Get the WindowProc for this window.
	WNDPROC oldProc = getProc((HWND)hwnd);

	// When we get a mouse wheel message
	if(uMsg == WM_MOUSEWHEEL)
	{
		MSG *pMsg = new MSG;
		pMsg->hwnd = hwnd;
		pMsg->message = uMsg;
		pMsg->wParam = wParam;
		pMsg->lParam = lParam;

		// send it to the java code.
		_beginthread(StartNotifyThread, 0, pMsg);
		return 0;
	}
	return ::CallWindowProc(oldProc, hwnd, uMsg, wParam, lParam);
}


// JNI native entry point for subclassing the window
JNIEXPORT void JNICALL Java_JFrameEx_setHook
  (JNIEnv *pEnv, jobject f, jint hwnd)
{
	// ensure that the java object can be called from any thread.
	jobject frame = pEnv->NewGlobalRef(f);

	WNDPROC oldProc = (WNDPROC)::SetWindowLong((HWND)hwnd, GWL_WNDPROC, (LONG)FrameWindowProc);

	// store the java object
	setFrame((HWND)hwnd, frame);
	// store the old window proc
	setProc ((HWND)hwnd, oldProc);
}

// JNI native entry point remving subclassing from the window
JNIEXPORT void JNICALL Java_JFrameEx_resetHook
  (JNIEnv *pEnv, jobject f, jint hwnd)
{
	WNDPROC oldProc = getProc((HWND)hwnd);
	jobject frame = getFrame((HWND)hwnd);
	::SetWindowLong((HWND)hwnd, GWL_WNDPROC, (LONG)oldProc);
	pEnv->DeleteGlobalRef(frame);
}

// JVM independent helper for getting the current JVM.
pJNI_GetCreatedJavaVMs getJavaVM()
{
	// Use the entrypoint of the current VM.
	HINSTANCE hMod = ::GetModuleHandle("msjava");
	if(hMod == NULL)
		hMod = ::GetModuleHandle("jvm");
	if(hMod == NULL)
		hMod = ::GetModuleHandle("javai");
	pJNI_GetCreatedJavaVMs pFunc = (pJNI_GetCreatedJavaVMs)::GetProcAddress(hMod,"JNI_GetCreatedJavaVMs");
	return pFunc;
}

// This helper is the one that actually invokes the java JFrameEx's notifyMouseWheel method
void notifyMouseWheel(jobject frame,short fwKeys,short zDelta,long xPos, long yPos)
{

	HMODULE hMod	= NULL;
	JavaVM *vmBuf	= NULL;
	JNIEnv *pEnv	= NULL;
	jsize bufLen	= 1;
	jint nVMs		= 0;

	// Get the Java VM.
	pJNI_GetCreatedJavaVMs pFunc = getJavaVM();
	jint nRet = (*pFunc)(&vmBuf,bufLen,&nVMs);

	// Attach this thread.
	vmBuf->AttachCurrentThread((void **)&pEnv,NULL);

	// Inform the java object that the child has been created.
    jclass cls = pEnv->GetObjectClass(frame);
    jmethodID mid = pEnv->GetMethodID(cls, "notifyMouseWheel", "(SSJJ)V");
    if (mid == 0)
        return;
    pEnv->CallVoidMethod(frame, mid, (jshort)fwKeys, (jshort)zDelta, (jlong)xPos, (jlong)yPos);

	// Detach this thread.
	vmBuf->DetachCurrentThread();
}

// Helper Thread for notification.
void WINAPIV StartNotifyThread(LPVOID lpVoid)
{
	MSG *pMsg = (MSG *)lpVoid;

	// This is the java object that needs to be notified.
	jobject frame = getFrame(pMsg->hwnd);

	// extract info from the wparam and lparam.
	WPARAM fwKeys = LOWORD(pMsg->wParam);
	short zDelta  = HIWORD(pMsg->wParam);

	LPARAM xPos = GET_X_LPARAM(pMsg->lParam);
	LPARAM yPos = GET_Y_LPARAM(pMsg->lParam);

	// call the helper function.
	notifyMouseWheel(frame,fwKeys,zDelta,xPos,yPos);

	delete pMsg;
}

