package cn.tz.cj.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class InputLengthLimit extends PlainDocument {

    private int len;

    public InputLengthLimit(int len) {
        this.len = len;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) return;
        if ((getLength() + str.length()) <= len) {
            super.insertString(offs, str, a);//调用父类方法
        }
    }

}
