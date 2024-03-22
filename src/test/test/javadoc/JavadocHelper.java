package test.javadoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;



public class JavadocHelper {

	public static HashMap<String, List<String>> getClassDocs(String fqcn) {
        HashMap<String, List<String>> jDoc = null;
        try {
            jDoc = extractClassSignaturesAndDocs(fqcn, false);
        } catch (IOException e) {
            return null;
        }
        return jDoc;
    }

    public static boolean allClassesAreDocumented(HashMap<String, List<String>> jDoc) {
        for (String sig: jDoc.keySet()) {
            if (jDoc.get(sig).size() == 0) return false;
            int linesBeforeTags = 0;
            boolean foundAt = false;
            boolean foundAuthor = false;
            boolean foundVersion = false;
            boolean isEnum = sig.contains("enum");
            for (String line: jDoc.get(sig)) {
                line = stripCommentSignifier(line);
                if (line.startsWith("@")) foundAt = true;
                if (!foundAt && line.length() > 0) ++linesBeforeTags;
                if (line.startsWith("@author ") && line.length() > "@author ".length()) foundAuthor = true;
                if (line.startsWith("@version ") && line.length() > "@version ".length()) foundVersion = true;
            }
            if (!isEnum && (!foundAuthor || !foundVersion)) return false;
            if (linesBeforeTags < 1) return false;
        }
        return true;
    }

    public static boolean _testclassIsDocumented(String fqcn, String parentFqcn) {
        HashMap<String, List<String>> jDoc = getClassDocs(parentFqcn);
        if (jDoc == null)
            return false;

        // Check the expected class is documented
        String classType = fqcn.contains("$") ? "enum" : "class";
        String[] parts = fqcn.split("\\."); 
        String simpleName = parts[parts.length - 1];
        if (classType.equals("enum")) {
            String[] parts2 = simpleName.split("\\$");
            simpleName = parts2[parts2.length - 1];
        }
          
        String key = null;
        for (String sig: jDoc.keySet()) {
            if (sig.matches(classType + "\\s*" + simpleName + ".*")) {
                key = sig;
                break;
            }
        }
        
        if (key == null)
            return false;
        if (jDoc.get(key).size() == 0)
            return false;

        // Check all public/protected classes in this file are documented
        return allClassesAreDocumented(jDoc);
	}

    public static boolean _testclassIsDocumented(String fqcn) {
        return _testclassIsDocumented(fqcn, fqcn);
	}

    public static Path fqcnToFilePath(String fqcn) throws IOException {
        // Note that split takes in a regex so we have to escape the dot here
        String[] elems = fqcn.split("\\.");
        elems[elems.length-1] += ".java";

        // Varargs would be nice here, but since we'll either have 2 or 3
        // elements it's not essential
        if (elems.length != 2 && elems.length !=3) {
          throw new IllegalArgumentException();
        }

        // Nasty hack because we're not running from inside the src directory
        List<String> tmp = new ArrayList<String>();
        tmp.add("src");
        tmp.add("main");
        Collections.addAll(tmp, elems);
        elems = tmp.toArray(new String[tmp.size()]);

        // Combination of the above two nasty hacks :(
        Path p = Paths.get(elems[0], elems[1], elems[2], elems[3]);
        if (elems.length > 4) {
          p = Paths.get(elems[0], elems[1], elems[2], elems[3], elems[4]);
        }

        return p.toAbsolutePath();
    }

    // Caveat: will only work if the signature is all on one line!
    // I don't think this is problematic given that students see whether or
    // not the test is failing and can easily adjust their code accordingly.
    public static String extractJavadocableSignature(String line) {
      line = line.strip();
      if (lineContainsModifiers(line)) {
          if (line.startsWith("public ") || line.startsWith("protected ")) {
              while (lineContainsModifiers(line)) {
                  line = line.split("\\s", 2)[1];
              }
              if (line.endsWith("{")) {
                  StringBuilder sb = new StringBuilder(line);
                  sb.deleteCharAt(line.length() - 1);
                  line = sb.toString().strip();
              }
              if (line.endsWith(";")) {
                  StringBuilder sb = new StringBuilder(line);
                  sb.deleteCharAt(line.length() - 1);
                  line = sb.toString().split("=", 2)[0].strip();
              }
              return line;
          }

      }
      return null;
    }

    public static String stripCommentSignifier(String line) {
        line = line.strip();
        if (line.endsWith("*/")) {
            line = line.substring(0, line.length()-2);
        }
        if (line.startsWith("/**")) {
            line = line.substring(3);
        } else if (line.startsWith("*")) {
            line = line.substring(1);
        }
        return line.strip();
    }

    public static boolean isAnnotation(String line) {
        line = line.strip();
        boolean simpleAnnotation = line.equals("@FunctionalInterface") || line.equals("@Override") || line.equals("@SafeVarargs");
        return simpleAnnotation || line.startsWith("@Deprecated") || line.startsWith("@SuppressWarnings");
    }

    public static boolean lineContainsModifiers(String line) {
        line = line.strip();
        boolean visibility = line.startsWith("public ") || line.startsWith("protected ") || line.startsWith("private ");
        boolean nonaccess = line.startsWith("final ") || line.startsWith("abstract ");
        if (!line.contains("class ") && !line.contains("enum")) {
            nonaccess = nonaccess || line.startsWith("static ") || line.startsWith("synchronized ");
            nonaccess = nonaccess || line.startsWith("transient ") || line.startsWith("volatile ");  
        }
        return visibility || nonaccess;
    }

    public static HashMap<String, List<String>> extractClassSignaturesAndDocs(String fqcn, boolean members) throws IOException {
        Path path = fqcnToFilePath(fqcn);
        List<String> lines = Collections.emptyList();
        lines = Files.readAllLines(path);
        Collections.reverse(lines);

        HashMap<String, List<String>> rtn = new HashMap<String, List<String>>();

        Iterator<String> itr = lines.iterator();
        String line = "";
        String currentMember = null;
        List<String> docLines = new ArrayList<String>();
        while (itr.hasNext()) {
            line = itr.next().strip();
            String sig = extractJavadocableSignature(line);
            boolean skipOverride = members ? isOverride(fqcn, sig) : false;
            boolean skipMemberType = false;
            if (sig != null) {
                boolean isClass = sig.contains("class") || sig.contains("enum");
                skipMemberType = isClass && members;
                skipMemberType = skipMemberType || (!isClass && !members);    
            }
            boolean skip = skipOverride || skipMemberType;

            if (sig != null || line.endsWith(";") || line.startsWith("}")) {
                if (currentMember != null && !members) {
                    Collections.reverse(docLines);
                    rtn.put(currentMember, docLines);
                } else if (currentMember != null && members) {
                    Collections.reverse(docLines);
                    rtn.put(currentMember, docLines);
                }
                currentMember = skip ? null : sig;
                docLines = new ArrayList<String>();
            }
            if (sig == null && currentMember != null & line != "" & !isAnnotation(line)) {
              docLines.add(line);
            }
        }

        // This will be necessary only if there are no empty lines or import statements before the first class javadoc
        // Unlikely but it will happen eventually
        if (currentMember != null && docLines.size() > 0) {
            Collections.reverse(docLines);
            rtn.put(currentMember, docLines);
        }

        return rtn;
    }

    public static boolean isOverride(String fqcn, String sigToCheck) throws IOException {
        Path path = fqcnToFilePath(fqcn);
        List<String> lines = Collections.emptyList();
        lines = Files.readAllLines(path);
        Collections.reverse(lines);

        String line;
        Iterator<String> itr = lines.iterator();
        boolean found = false;
        while (itr.hasNext()) {
            line = itr.next().strip();
            String sig = extractJavadocableSignature(line);
            if (sig != null && sig.equals(sigToCheck)) {found = true; continue;}
            if (found) {
                if (!isAnnotation(line)) break;
                if (line.equals("@Override")) return true;
            }
        }
        return false;
    }

    public static HashMap<String, List<String>> getMemberDocs(String fqcn) {
        HashMap<String, List<String>> jDoc = null;
        try {
            jDoc = extractClassSignaturesAndDocs(fqcn, true);
        } catch (IOException e) {
            return null;
        }
        return jDoc;
    }

    public static boolean allMembersAreDocumented(String fqcn) {
        HashMap<String, List<String>> jDoc = getMemberDocs(fqcn);
        if (jDoc == null) return false;

        for (String sig: jDoc.keySet()) {
            if (jDoc.get(sig).size() == 0) return false;
            if (!hasAppropriateTags(jDoc.get(sig), sig, fqcn)) return false;
        }
        return true;
    }

    public static List<String> getParams(String sig) throws ArrayIndexOutOfBoundsException {
        List<String> rtn = new ArrayList<String>();
        String params = "";
        try {
            params = sig.split("\\(", 2)[1].split("\\)")[0].strip();        
        } catch (ArrayIndexOutOfBoundsException e) {}
        if (params.equals("")) return rtn;

        for (String p : params.split(",")) {
            String[] tmp = p.strip().split("\\s");
            rtn.add(tmp[tmp.length - 1]);
        }
        return rtn;
    }

    public static List<String> getThrows(String sig) throws ArrayIndexOutOfBoundsException {
        List<String> rtn = new ArrayList<String>();
        String throwsStr = "";
        try {
            throwsStr = sig.split("throws ")[1].strip();  
        } catch (ArrayIndexOutOfBoundsException e) {}
        if (throwsStr.equals("")) return rtn;

        for (String t : throwsStr.split(",")) {
            rtn.add(t.strip());
        }
        return rtn;
    }

    public static boolean returnsValue(String fqcn, String sig) throws ArrayIndexOutOfBoundsException {
        String [] parts = sig.split("\\(");
        if (parts.length == 1) return false;
        parts = parts[0].strip().split("\\s");
        if (parts.length == 1) return false;
        return !parts[0].strip().equals("void");
    }

    public static boolean hasAppropriateTags(List<String> javadocLines, String sig, String fqcn) {
        List<String> throwsExpected = getThrows(sig);
        List<String> paramsExpected = getParams(sig);
        List<String> paramsFound = new ArrayList<String>();
        List<String> throwsFound = new ArrayList<String>();
        int linesBeforeTags = 0;
        boolean foundAt = false;
        boolean foundReturn = false;
        for (String line: javadocLines) {
            line = stripCommentSignifier(line);
            if (line.startsWith("@")) foundAt = true;
            if (line.startsWith("@return ") && line.split("\\s").length > 1) foundReturn = true;
            if (!foundAt && line.length() > 0) ++linesBeforeTags;
            if (line.startsWith("@param")) {
                String[] parts = line.substring(6).strip().split("\\s");
                if (parts.length > 1) {
                    paramsFound.add(parts[0]);
                }
            } else if (line.startsWith("@throws")) {
                String[] parts = line.substring(7).strip().split("\\s");
                if (parts.length > 1) {
                    throwsFound.add(parts[0]);
                }
            }
        }
        if (linesBeforeTags < 1) return false;
        if (!paramsExpected.equals(paramsFound)) return false;
        if (!throwsExpected.equals(throwsFound)) return false;
        if (foundReturn != returnsValue(fqcn, sig)) return false;
        return true;
    }

    public static boolean _testmemberIsDocumented(String fqcn, String memberRegex) {
        HashMap<String, List<String>> jDoc = getMemberDocs(fqcn);
        if (jDoc == null)
            return false;

        // Check the expected member is documented
        String key = null;
        for (String sig: jDoc.keySet()) {
            if (sig.matches(memberRegex)) {
                key = sig;
                break;
            }
        }
        if (key == null)
            return false;
        if (jDoc.get(key).size() == 0)
            return false;
        return hasAppropriateTags(jDoc.get(key), key, fqcn);
	}


  }
