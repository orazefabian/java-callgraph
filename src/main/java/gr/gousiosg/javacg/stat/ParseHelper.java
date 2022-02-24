package gr.gousiosg.javacg.stat;

public class ParseHelper {

    public static String getInterfacesAsString(String[] interfaceStrings) {
        String present = "[";
        for (int i = 0; i < interfaceStrings.length; i++) {
            present += interfaceStrings[i];
            if (i != interfaceStrings.length - 1) {
                present += ",";
            }
        }
        present += "]";
        return present;
    }
}