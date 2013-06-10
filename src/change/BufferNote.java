package change;

import graphic.textbox.TextBoxCommentary;

public class BufferNote implements Changeable
{
  private TextBoxCommentary note;
  private String text;
  
  public BufferNote(TextBoxCommentary note)
  {
    this.note = note;
    this.text = note.getText();
  }

  @Override
  public void restore()
  {
    note.setText(text);
  }

}
