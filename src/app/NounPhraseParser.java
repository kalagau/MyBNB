package app;

import java.io.*;
import java.util.*;
import opennlp.tools.parser.*;
import opennlp.tools.cmdline.parser.ParserTool;

public class NounPhraseParser {

    private static  Map<String,Integer> nounPhrases = new HashMap<String,Integer>();

    private static void getNounPhrases(Parse p) {
        if (p.getType().equals("NP")) {
            if (nounPhrases.containsKey(p.getCoveredText())) {
                nounPhrases.put(p.getCoveredText(), nounPhrases.get(p.getCoveredText()) + 1);
            } else {
                nounPhrases.put(p.getCoveredText(),1);
            }
        }

        for (Parse child : p.getChildren()) {
            getNounPhrases(child);
        }
    }
    public static ArrayList<String> parseNouns(ArrayList<String> sentences) throws Exception{
        ArrayList<String> list= new ArrayList<String>();
        nounPhrases = new HashMap<String,Integer>();

        InputStream is = new FileInputStream("en-parser-chunking.bin");

        ParserModel model = new ParserModel(is);

        Parser parser = ParserFactory.create(model);

        for (String current : sentences){
            Parse topParses[] = ParserTool.parseLine(current, parser, 1);

            for (Parse p : topParses)
                getNounPhrases(p);

        }
        is.close();
        // only keep NP's where they occur more than once
        for (String key : nounPhrases.keySet()) {
            if (nounPhrases.get(key) > 0)
                list.add(key);
        }
        return list;


    }
}
