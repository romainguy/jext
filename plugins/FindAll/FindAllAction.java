import org.jext.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class FindAllAction extends MenuAction
{
  FindAll findAll = null;
  
  public FindAllAction()
  {
    super("FindAllAction");
  }
  
  public void setFindAll(FindAll findAll)
  {
    this.findAll = findAll;
  }
  
  public void actionPerformed(ActionEvent evt)
  {
    if (findAll != null)
      findAll.findText();
  }
}
