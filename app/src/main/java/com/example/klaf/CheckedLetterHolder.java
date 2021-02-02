package com.example.klaf;

public class CheckedLetterHolder {
    private String letter;
    boolean checked;

    public CheckedLetterHolder(String letter, boolean checked) {
        this.letter = letter;
        this.checked = checked;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "{" + letter  + " " + checked + "}";
    }
}
