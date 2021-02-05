package com.example.klaf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IpaProcesser {

    public String getInCompletedCouples(List<CheckedLetterHolder> checkedLetterHolders) {
        List<String> list = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < checkedLetterHolders.size(); i++) {
            CheckedLetterHolder checkedHolder = checkedLetterHolders.get(i);

            if (checkedHolder.isChecked()) {
                if (!list.isEmpty() && list.get(list.size() - 1).equals(" = ")) {
                    list.add("\n");
                }
                list.add(checkedLetterHolders.get(i).getLetter());
            } else if (!list.isEmpty() && !list.get(list.size() - 1).equals(" = ")) {
                list.add(" = ");
            }
        }

        if (!list.isEmpty() && !list.get(list.size() - 1).equals(" = ")) {
            list.add(" = ");
        }
        for (String s : list) {
            result.append(s);
        }
        return result.toString();
    }

    public String getCodedIpaForDB(List<CheckedLetterHolder> checkedHolders, String ipaTemplate) {
        StringBuilder result = new StringBuilder();
        String clearedIpaTemplate = ipaTemplate.replaceAll("\\s*=[^\n\\w]*", "=");
        StringBuilder sbIpaTemplate = new StringBuilder(clearedIpaTemplate);

        for (int i = 0; i < checkedHolders.size(); i++) {
            CheckedLetterHolder holder = checkedHolders.get(i);

            if (holder.isChecked()) {
                if (i == 0) {
                    result.append("/");
                } else if ((i > 0 && !checkedHolders.get(i - 1).isChecked())) { //  || sbIpaTemplate.toString().startsWith("\n")
                    result.append("/");
                } else if (i > 0 && checkedHolders.get(i - 1).isChecked()) {
                    result.append("//");
                }

                String ipaCouple;
                if (sbIpaTemplate.toString().substring(1).contains("\n")) {
                    if (sbIpaTemplate.toString().startsWith("\n")) {
//                        sbIpaTemplate = sbIpaTemplate.substring(1);
                        sbIpaTemplate = new StringBuilder(sbIpaTemplate.substring(1));
//                        ipaCouple = sbIpaTemplate.substring(1, sbIpaTemplate.indexOf("\n", 1));
                    } // else {
                    ipaCouple = sbIpaTemplate.substring(0, sbIpaTemplate.indexOf("\n"));
                    //  }
                } else {
                    if (sbIpaTemplate.toString().startsWith("\n")) {
                        sbIpaTemplate = new StringBuilder(sbIpaTemplate.substring(1));
                    }
                    ipaCouple = sbIpaTemplate.substring((0));
                }
                String replacedLetters = sbIpaTemplate.substring(0, sbIpaTemplate.indexOf("="));
                i += replacedLetters.length() - 1;
                result.append(ipaCouple);
                sbIpaTemplate.delete(0, ipaCouple.length());

            } else {
                if (i > 0 && checkedHolders.get(i - 1).isChecked()) {
                    result.append("/");
                }
                result.append(holder.getLetter());
            }
        }
        return result.toString();
    }


    public List<CheckedLetterHolder> getDecodeSoundsListFromIpa(String codedIpa) {
        StringBuilder ipa = new StringBuilder(codedIpa);
        List<CheckedLetterHolder> result = new ArrayList<>();

        while (!ipa.toString().equals("")) {

            if (ipa.toString().startsWith("/")) {
                String codedLetters;
                String letters;
                String sound;

                if (ipa.substring(1).contains("/")) {
                    codedLetters = ipa.substring(0, ipa.indexOf("/", 1) + 1);
                    ipa.delete(0, ipa.indexOf("/", 1) + 1);
                    sound = codedLetters.substring(codedLetters.indexOf("=") + 1, codedLetters.indexOf("/", 1));

                } else {  // /fdsa=j
                    codedLetters = ipa.substring(0);
                    ipa.delete(0, ipa.length() + 1);
                    sound = codedLetters.substring(codedLetters.indexOf("=") + 1);
                }

                letters = codedLetters.substring(1, codedLetters.indexOf("="));

                int letterLength = letters.length();
                int soundLength = sound.length();

                if (letterLength > soundLength) {
                    double d = ((double) letterLength - (double) soundLength) / 2;
                    int n = (letterLength - soundLength) / 2;
                    int indexForPutting = d > n ? n + 1 : n;

                    for (int j = 0, q = 0; j < letterLength; j++) {
                        if (j < indexForPutting || soundLength <= q) {
                            result.add(new CheckedLetterHolder(letters.substring(j, j + 1), false));
                        } else {
                            result.add(new CheckedLetterHolder(sound.substring(q, q + 1), true));
                            q++;
                        }
                    }
                } else {
                    for (int w = 0; w < soundLength; w++) {
                        result.add(new CheckedLetterHolder(sound.substring(w, w + 1), true));
                    }
                }

            } else {
                result.add(new CheckedLetterHolder(ipa.substring(0, 1), false));
                ipa.delete(0, 1);
            }
        }
        return result;
    }

    public List<CheckedLetterHolder> getCheckedLetterHolder(String codedIpaFromDB) {
        List<CheckedLetterHolder> result = new ArrayList<>();
        StringBuilder builderForeignWord = new StringBuilder(codedIpaFromDB);
        //  /th=ø//ough=o/t
        while (builderForeignWord.length() > 0) {

            if (builderForeignWord.substring(0, 1).equals("/")) {
                builderForeignWord.delete(0, 1);
                int indexEquals = builderForeignWord.indexOf("=");
                String letters = builderForeignWord.substring(0, indexEquals);
                for (int q = 0; q < letters.length(); q++) {
                    result.add(new CheckedLetterHolder(letters.substring(q, q + 1), true));
                }
                if (builderForeignWord.toString().contains("/")) {
                    builderForeignWord.delete(0, builderForeignWord.indexOf("/") + 1);
                } else {
                    builderForeignWord.delete(0, builderForeignWord.length());
                }
            } else {
                result.add(new CheckedLetterHolder(builderForeignWord.substring(0, 1), false));
                builderForeignWord.delete(0, 1);
            }
        }
        return result;
    }

    public String getCompletedCouples(String codedIpa) {
        StringBuilder result = new StringBuilder();
        StringBuilder ipa = new StringBuilder(codedIpa);

        while (ipa.length() > 0) {
            if (ipa.substring(0, 1).equals("/")) {
                ipa.delete(0, 1);
                String couple;
                if (ipa.toString().contains("/")) {
                    int index = ipa.indexOf("/");
                    couple = ipa.substring(0, index) + "\n";
                    ipa.delete(0, index + 1);
                } else {
                    couple = ipa.substring(0, ipa.length());
                    ipa.delete(0, ipa.length());
                }
                result.append(couple);
            } else {
                ipa.delete(0, 1);
            }
        }
        if (result.toString().endsWith("\n")) {
            int resultLength = result.length();
            result.delete(resultLength - 1, resultLength);
        }
        return result.toString();
    }
}
