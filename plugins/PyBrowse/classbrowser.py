"A Python source code browser for Jext."
## 06/14/2001 - 22:02:54
##
## A Python class/methods browser for Jext
## Written in Python/Swing, using a modified pyclbr Python library
## Based on ClassBrowser module from IDLE v0.8
##
## (C)2001 Romain Guy
## romain.guy@jext.org
## www.jext.org

##
## Jext & Java CLASSES
##
from org.jext import *
from org.jext.event import *
from java.lang import *
from java.awt import *
from java.awt.event import *
from javax.swing import *
from javax.swing.event import *
from javax.swing.tree import *

##
## USED BY load() METHOD
##
import os
import pyclbr
import string
import sys

# SELECTION
class SelectionHandler(TreeSelectionListener):
  "handles tree selections"
  def valueChanged(self, evt):
    "called on selection change"
    no = evt.getPath().getLastPathComponent().lineno
    if no != -1:
      textArea = __jext__.getTextArea()
      lelement = textArea.getDocument().getDefaultRootElement().getElement(no - 1)
      textArea.select(lelement.getStartOffset(), lelement.getEndOffset() - 1)

# TREE NODES
class ClassNode(DefaultMutableTreeNode):
  "a class or a method is represented by its name and its line number"
  def __init__(self, name, lineno):
    DefaultMutableTreeNode.__init__(self, name)
    self.name = name
    self.lineno = lineno

# BROWSER
class ClassBrowser(JPanel, JextListener, Runnable):
  "a Python source code browser for Jext"
  def __init__(self):
    JPanel.__init__(self, BorderLayout())
    __jext__.addJextListener(self)
    self.browserTree = JTree()
    self.seeker = None

    renderer = DefaultTreeCellRenderer()
    renderer.setOpenIcon(Utilities.getIcon("images/tree_open.gif", __jext__.class))
    renderer.setLeafIcon(Utilities.getIcon("images/tree_leaf.gif", __jext__.class))
    renderer.setClosedIcon(Utilities.getIcon("images/tree_close.gif", __jext__.class))

    renderer.setTextSelectionColor(GUIUtilities.parseColor(Jext.getProperty("vf.selectionColor")))
    renderer.setBackgroundSelectionColor(self.browserTree.getBackground())
    renderer.setBorderSelectionColor(self.browserTree.getBackground())

    self.browserTree.setCellRenderer(renderer)
    self.browserTree.putClientProperty("JTree.lineStyle", "Angled")
    self.browserTree.setScrollsOnExpand(1);

    selectionModel = DefaultTreeSelectionModel();
    selectionModel.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
    self.browserTree.setSelectionModel(selectionModel);

    self.load(__jext__.getTextArea())

    self.browserTree.expandRow(0)
    self.browserTree.setRootVisible(0)
    self.browserTree.setShowsRootHandles(1)
    self.browserTree.addTreeSelectionListener(SelectionHandler())

    self.add(JScrollPane(self.browserTree), BorderLayout.CENTER)

  # jext events
  def jextEventFired(self, evt):
    "handles events fired by Jext"
    evtType = evt.getWhat()
    # on selection
    if evtType == JextEvent.TEXT_AREA_SELECTED or evtType == JextEvent.TEXT_AREA_OPENED or \
       evtType == JextEvent.TEXT_AREA_CLOSED:
      #if not evt.getJextFrame().getBatchMode():
      self.load(evt.getTextArea())
    # on textarea change
    elif evtType == JextEvent.CHANGED_UPDATE or evtType == JextEvent.REMOVE_UPDATE or \
         evtType == JextEvent.INSERT_UPDATE:
      # threaded operation
      if not self.seeker is None:
        self.seeker.stop()
        self.seeker = None
      self.seeker = Thread(self)
      self.seeker.start()

  def run(self):
    "waiting loop"
    try:
      Thread.sleep(400)
    except:
      pass
    self.load(__jext__.getTextArea())

  # browses the opened Python file and creates a list of classes and methods
  def load(self, textArea):
    "loads a new tree from a given text area"
    # create tree root
    root = DefaultMutableTreeNode("root")
    browserTreeModel = DefaultTreeModel(root)

    if textArea is None:
      return
    #textArea = __jext__.getTextArea()
    # not a python file !
    if textArea.getColorizingMode() != "python":
      root.add(ClassNode("Not a python file !", -1))
      self.browserTree.setModel(browserTreeModel)
      return

    # python file need to be saved
    file = textArea.getCurrentFile()
    dir = "."
    if file == None:
      file = textArea.getName() + ".py"
    else:
      dir, file = os.path.split(file)
    name, ext = os.path.splitext(file)
    pyclbr._modules = {}
    #dic = pyclbr.readmodule_ex(name, [dir] + sys.path)
    dic = pyclbr.readsource_ex(name, file, textArea.getText())

    self.items = []
    self.classes = {}
    s = ''

    for key, cl in dic.items():
      if cl.module == name:
        s = key
        if cl.super:
          supers = []
          for sup in cl.super:
            if type(sup) is type(''):
              sname = sup
            else:
              sname = sup.name
              if sup.module != cl.module:
                sname = "%s.%s" % (sup.module, sname)
            supers.append(sname)
          s = s + "(%s)" % string.join(supers, ", ")
        self.items.append((cl.lineno, s))
        self.classes[s] = cl
    self.items.sort()
    node = None
    for item, cname in self.items:
      if isinstance(self.classes[cname], pyclbr.Function):
        node = ClassNode("def " + cname + "()", item)
      else:
        node = ClassNode("class " + cname, item)
        for name, lineno in self.classes[cname].methods.items():
          node.add(ClassNode("def " + name + "()", lineno))
        if node.getChildCount() == 0:
          node.add(ClassNode("<none>", -1))
      root.add(node)
    self.browserTree.setModel(browserTreeModel)

# MAIN ENTRY POINT
def main():
  "main method"
  # if the tab already exists
  # we remove it then add it once more
  tPane = __jext__.getVerticalTabbedPane()
  if tPane.indexOfTab("PyBrowser") == -1:
    tPane.addTab("PyBrowser", ClassBrowser())
  else:
    __jext__.removeJextListener(tPane.getComponentAt(tPane.indexOfTab("PyBrowser")))
    tPane.removeTabAt(tPane.indexOfTab("PyBrowser"))

if __name__ == '__main__':
  main()


  # End of classbrowser.py
