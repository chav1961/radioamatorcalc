package chav1961.calc;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

// First, we create the ClipboardListener to listen on the machine new things copied
// Our listener will extends Thread to run in background
public class ClipboardListener implements ClipboardOwner {

  // Entry Listener. It will be useful for class wanting to be alerted when a new entry is copied
  interface EntryListener {
    void onCopy(String data);
  }

  private EntryListener entryListener;

  public void setEntryListener(EntryListener entryListener) {
    this.entryListener = entryListener;
  }

  @Override
  public void lostOwnership(Clipboard c, Transferable t) {
    try {
      Thread.sleep(200);
    } catch (Exception e) {
    }

    Transferable contents = c.getContents(this);
    processContents(contents);
    regainOwnership(c, contents);
  }

  public void processContents(Transferable t) {
    try {
      String what = (String) (t.getTransferData(DataFlavor.stringFlavor));

      // we alert our entry listener
      if (entryListener != null) {
        entryListener.onCopy(what);
      }
    } catch (Exception e) {
    }
  }

  public void regainOwnership(Clipboard c, Transferable t) {
    c.setContents(t, this);
  }

  public void run() {
	final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
   Transferable transferable = clipboard.getContents(this);
    regainOwnership(clipboard, transferable);

//    while(true);
  }

}