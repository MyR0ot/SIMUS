package logic;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.ParseException;

public class MyJSpinner extends JSpinner {
    boolean setvalueinprogress=false;

    public MyJSpinner()
    {
        super();
        final JTextField jtf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
        jtf.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                showChangedValue(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                showChangedValue(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                showChangedValue(e);
            }

            private void showChangedValue(DocumentEvent e){
                try {
                    if (!setvalueinprogress)
                        MyJSpinner.this.commitEdit();
                } catch (NumberFormatException | ParseException ex) {
                    //handle if you want
                    // Exceptions.printStackTrace(ex);
                }
            }
        });
    }

    @Override
    public void setValue(Object value) {
        setvalueinprogress=true;
        super.setValue(value);
        setvalueinprogress=false;
    }

}
