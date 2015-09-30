package gov.nist.toolkit.actorfactory;

import java.io.*;

/**
 * Created by bill on 9/30/15.
 */
public class Serialize {

    static public void out(File destination, Object subject) throws IOException {
            FileOutputStream fileOut = new FileOutputStream(destination);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(subject);
            out.close();
            fileOut.close();
    }

    static public Object in(File source) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(source);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Object obj = in.readObject();
        in.close();
        fileIn.close();
        return obj;
    }
}
