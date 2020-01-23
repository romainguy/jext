/*
 * 03/30/2002 - 15:56:05
 *
 * JextMail.java - Mail document
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.misc;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import org.jext.*;
import org.jext.gui.*;

import javax.swing.*;
import javax.swing.text.Element;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;

import java.net.URL;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JextMail extends JDialog implements ActionListener, Runnable
{
  private int y = 0;
  private JPanel pane;
  private JextFrame parent;
  private Thread mailer;
  private JTextArea tracer;
  private boolean traceState;
  private JScrollPane scroller;
  private JextTextArea textArea;
  private GridBagLayout gridBag;
  private JextHighlightButton send, cancel, details;
  private JTextField host, from, to, subject;
  
  protected void addComponent(String label, Component comp)
  {
    GridBagConstraints cons = new GridBagConstraints();
    cons.gridy = y++;
    cons.gridheight = 1;
    cons.gridwidth = 3;
    cons.fill = GridBagConstraints.BOTH;
    cons.weightx = 1.0f;
    cons.insets = new Insets(2, 2, 2, 2);

    cons.gridx = 0;
    JLabel l = new JLabel(label, SwingConstants.RIGHT);
    gridBag.setConstraints(l, cons);
    pane.add(l);

    cons.gridx = 3;
    cons.gridwidth = 1;

    gridBag.setConstraints(comp, cons);
    pane.add(comp);
  }

  public JextMail(JextTextArea textArea)
  {
    super(textArea.getJextParent(), Jext.getProperty("mail.title"), false);
    this.textArea = textArea;
    parent = textArea.getJextParent();

    getContentPane().setLayout(new BorderLayout());
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

    pane = new JPanel();
    pane.setLayout(gridBag = new GridBagLayout());

    addComponent(Jext.getProperty("mail.host.label"), (host = new JTextField(15)));
    host.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    addComponent(Jext.getProperty("mail.from.label"), (from = new JTextField(15)));
    from.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    addComponent(Jext.getProperty("mail.to.label"), (to = new JTextField(15)));
    to.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    addComponent(Jext.getProperty("mail.subject.label"), (subject = new JTextField(15)));
    subject.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    JPanel btnPane = new JPanel();
    btnPane.add((send = new JextHighlightButton(Jext.getProperty("mail.send.button"))));
    send.setToolTipText(Jext.getProperty("mail.send.tip"));
    send.setMnemonic(Jext.getProperty("mail.send.mnemonic").charAt(0));
    send.addActionListener(this);
    getRootPane().setDefaultButton(send);

    btnPane.add((cancel = new JextHighlightButton(Jext.getProperty("general.cancel.button"))));
    cancel.setMnemonic(Jext.getProperty("general.cancel.mnemonic").charAt(0));
    cancel.addActionListener(this);

    btnPane.add((details = new JextHighlightButton(Jext.getProperty("mail.details.expand.button"))));
    details.setMnemonic(Jext.getProperty("mail.details.mnemonic").charAt(0));
    details.addActionListener(this);

    tracer = new JTextArea(5, 15);
    tracer.setEditable(false);
    scroller = new JScrollPane(tracer,
                               ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                               ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

    getContentPane().add(pane, BorderLayout.NORTH);
    getContentPane().add(btnPane, BorderLayout.CENTER);

    load();

    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent evt)
      {
        save();
        parent.hideWaitCursor();
        dispose();
      }
    });

    addKeyListener(new AbstractDisposer(this));

    pack();
    setResizable(false);
    Utilities.centerComponentChild(parent, this);
    setVisible(true);
  }

  private void load()
  {
    host.setText(Jext.getProperty("host"));
    from.setText(Jext.getProperty("from"));
    to.setText(Jext.getProperty("to"));
    subject.setText(Jext.getProperty("subject"));
  }

  private void save()
  {
    Jext.setProperty("host", host.getText());
    Jext.setProperty("from", from.getText());
    Jext.setProperty("to", to.getText());
    Jext.setProperty("subject", subject.getText());
  }

  private void wait(boolean on)
  {
    send.setEnabled(!on);
    host.setEnabled(!on);
    to.setEnabled(!on);
    from.setEnabled(!on);
    subject.setEnabled(!on);

    if (on)
      parent.showWaitCursor();
    else
      parent.hideWaitCursor();
  }

  private void send()
  {
    if (!check())
      return;

    mailer = new Thread(this);
    mailer.setPriority(Thread.MIN_PRIORITY);
    mailer.setName("JextMail");
    mailer.start();
  }

  public void stop()
  {
    mailer = null;
  }

  public void run()
  {
    if (mailer != null)
    {
      wait(true);
      if (sendMail(host.getText(), from.getText(), to.getText(), subject.getText()))
        Utilities.showMessage(Jext.getProperty("mail.successfully"));
      else
        Utilities.showMessage(Jext.getProperty("mail.cannot"));
      wait(false);
    }
    stop();
  }

  private boolean check()
  {
    if (host.getText().equals("") || host.getText() == null)
    {
      Utilities.showMessage("Jext Mail", Jext.getProperty("mail.host"));
      return false;
    }
    if (from.getText().equals("") || from.getText().indexOf('@') == -1 ||
        to.getText().equals("") || to.getText().indexOf('@') == -1)
    {
      Utilities.showMessage("Jext Mail", Jext.getProperty("mail.email"));
      return false;
    }
    return true;
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object obj = evt.getSource();
    if (obj == send)
      send();
    else if (obj == details)
      showDetails();
    else if (obj == cancel)
    {
      save();
      dispose();
    }
  }

  private void showDetails()
  {
    if (traceState)
    {
      getContentPane().remove(scroller);
      details.setText(Jext.getProperty("mail.details.expand.button"));
    } else {
      getContentPane().add(scroller, BorderLayout.SOUTH);
      details.setText(Jext.getProperty("mail.details.collapse.button"));
    }

    pack();
    Utilities.centerComponentChild(parent, this);
    traceState = !traceState;
  }

  private void trace(String s)
  {
    tracer.append(s + "\n");
    tracer.setCaretPosition(tracer.getDocument().getLength());
  }

  private boolean error(String s)
  {
    Utilities.showMessage("Jext Mail", Jext.getProperty("mail." + s + ".msg"));
    return false;
  }

  /**
   * This mails area's contents trhough mail.
   * @param host The SMTP host address
   * @param from The sender's mail
   * @param to The receiver's mail
   * @param subject Mail's subject
   * @return True if no error has occured, false otherwise
   */

  public boolean sendMail(String host, String from, String to, String subject)
  {
    Socket smtpPipe;
    BufferedReader in;
    InetAddress ourselves;
    OutputStreamWriter out;

    try
    {
      ourselves = InetAddress.getLocalHost();
    } catch (UnknownHostException uhe) { return false; }

    tracer.setText("");

    int index = host.indexOf(':');
    int port = 25;
    if (index != -1)
    {
      port = Integer.parseInt(host.substring(index + 1));
      host = host.substring(0,index);
    }

    try
    {
      smtpPipe = new Socket(host, port);
      if (smtpPipe == null)
        return false;

      in = new BufferedReader(new InputStreamReader(smtpPipe.getInputStream()));
      out = new OutputStreamWriter(smtpPipe.getOutputStream());
      if (in == null || out == null)
        return false;

      String response, command;
      trace(response = in.readLine());
      if (!response.startsWith("220"))
        return error("serverdown");

      command = "HELO " + ourselves.getHostName();
      out.write(command + "\r\n");
      out.flush();
      trace(command);
      trace(response = in.readLine());
      if (!response.startsWith("250"))
        return error("badhost");

      command = "MAIL FROM:<" + from + ">";
      out.write(command + "\r\n");
      out.flush();
      trace(command);
      trace(response = in.readLine());
      if (!response.startsWith("250"))
        return error("badsender");

      command = "RCPT TO:<" + to + ">";
      out.write(command + "\r\n");
      out.flush();
      trace(command);
      trace(response = in.readLine());
      if (!response.startsWith("250"))
        return error("badrecepient");

      out.write("DATA\r\n");
      out.flush();
      trace("DATA");
      trace(response = in.readLine());
      if (!response.startsWith("354"))
        return error("badmsg");
      trace("[Sending mail...]");

      out.write("To: <" + to + ">");
      out.write("\r\n");
      out.write("From: <" + from + ">");
      out.write("\r\n");
      out.write("Subject: " + subject);
      out.write("\r\n");
      out.write("X-Mailer: Jext " + Jext.BUILD);
      out.write("\r\n");
      out.write("\r\n");

      try
      {
        String text;
        Document doc = textArea.getDocument();
        Element map = doc.getDefaultRootElement();
        int total = map.getElementCount();

        for (int i = 0; i < total; i++)
        {
          Element lineElement = map.getElement(i);
          int start = lineElement.getStartOffset();
          int end = lineElement.getEndOffset() - 1;
          end -= start;

          text = doc.getText(start, end);
          if (text.equals("."))
            text = "!";
          out.write(text);
          out.write("\r\n");
          trace("[" + (i + 1) * 100 / total + "%]");
        }
      } catch (BadLocationException ble) { }

      out.write(".\r\n");
      out.flush();
      trace(".");
      trace(response = in.readLine());
      if (!response.startsWith("250"))
        return error("badmsg");

      out.write("QUIT");
      trace("QUIT");

      smtpPipe.close();
      smtpPipe = null;
    } catch (IOException ioe) { return false; }

    return true;
  }
  

}

// End of JextMail.java