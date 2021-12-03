package change;

import graphic.textbox.TextBoxCommentary;

public class BufferNote extends BufferGraphicView {
  private TextBoxCommentary note;
  private String text;

  public BufferNote(TextBoxCommentary note) {
    super(note.getGraphicView());

    this.note = note;
    this.text = note.getText();
  }

  @Override
  public void restore() {
    super.restore();
    note.setText(text);
  }

  @Override
  public Object getAssociedComponent() {
    return note;
  }

}
