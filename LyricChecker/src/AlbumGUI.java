import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.IOException;
import musixmatch.*;

//we should make it show something in the output when the 
//"MusixMatchExeption: Api lookup did not return successfully:404" instead of it just being blank

public class AlbumGUI implements KeyListener
{
  private JTextField artistField;
  private JTextField albumField;
  private JTextArea output;
  private ArrayList<String> badWords;
  private ArrayList<String> qWords;
  
  public AlbumGUI(ArrayList<String> bWords, ArrayList<String> questionWords)
  { 
    badWords = bWords;
    qWords = questionWords;
    
    JFrame as = new JFrame("Album/Artist");
    as.setTitle("Search by album");
    
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(BorderFactory.createEmptyBorder(6, 10, 10, 10)); //top left bottom right
    
    JLabel art = new JLabel("Artist");
    art.setFont(art.getFont ().deriveFont (14.0f));
    art.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    p.add(art);
    
    artistField = new JTextField(30);
    artistField.setMaximumSize(artistField.getPreferredSize());
    p.add(artistField);
    artistField.addKeyListener(this);
    
    p.add(Box.createRigidArea(new Dimension(0,3))); //space between artfield/"song"
    
    JLabel album = new JLabel("Album");
    album.setFont(album.getFont ().deriveFont (14.0f));
    album.setAlignmentX(Component.CENTER_ALIGNMENT);
    p.add(album);
    
    albumField = new JTextField(30);
    albumField.setMaximumSize(albumField.getPreferredSize());
    albumField.addKeyListener(this);
    p.add(albumField);
    
    p.add(Box.createRigidArea(new Dimension(0,10)));
    
    JLabel info = new JLabel("Input artist & album and press enter.");
    info.setAlignmentX(Component.CENTER_ALIGNMENT);
    p.add(info);
    p.add(Box.createRigidArea(new Dimension(0,3))); 
    
    output = new JTextArea();
    output.setFont(new Font("monospaced", Font.PLAIN, 14));
    output.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    output.setLineWrap(true);
    output.setWrapStyleWord(true);
    output.setEditable(false);
    JScrollPane pane = new JScrollPane();
    pane.getViewport().add(output);
    p.add(pane);
    
    as.add(p);
    as.setSize(520,300);
    as.setLocationRelativeTo(null);
    as.setVisible(true);  
  }
  public void keyPressed(KeyEvent e) 
  {
    int key = e.getKeyCode();
    if (key == KeyEvent.VK_ENTER) 
    { 
      output.setBackground(Color.WHITE);
      runAlbumCheck();
      output.setCaretPosition(0);
      albumField.requestFocusInWindow();
      albumField.selectAll();
    }
  }
  public void runAlbumCheck() {
    try {
      output.setText("");
      Playlist album = MusixMatch.albumSearch(albumField.getText(), artistField.getText());
      if (album == null)  {
        output.setText("No such album.");
        return;
      }

      for (Track t : album) {
        output.append(MenuGUI.makeLevelLeft(t.getName())); 
        LyricChecker lc = new LyricChecker(t.getName(), t.getArtist(), badWords, qWords);
        lc.checkLyrics();
        if (lc.hasBadWords()) output.append(MenuGUI.makeLevelRight("[Explicit]"));
        else if (lc.hasQWords()) output.append(MenuGUI.makeLevelRight("[Questionable]"));
        else if (lc.lookupFailed()) output.append(MenuGUI.makeLevelRight(t.isExplicit() ? "[Explicit]" : "[Clean]"));
        else output.append(MenuGUI.makeLevelRight("[Clean]"));
        output.append("\r\n");
      }
//      try{    //trying to remove last blank line, doesn't work
//      int numLines = output.getLineCount();
//      output.replaceRange("", output.getLineEndOffset(numLines-2), output.getLineStartOffset(numLines-1));
//      }
//      catch(BadLocationException e) {
//        System.out.println("sucks");
//        return;
//      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void keyTyped(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
}