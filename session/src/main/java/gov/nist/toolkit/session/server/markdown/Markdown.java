package gov.nist.toolkit.session.server.markdown;

/**
 *
 */
public class Markdown {
    static public String toHtml(String markdown) {
        if (markdown.contains("<p>"))
            return markdown;
        StringBuilder buf = new StringBuilder();
        boolean lastIsPlain = true;
        String lastLine = "";

        String[] lines = markdown.split("\n");

        for (int i=0; i<lines.length; i++) {
            String line = lines[i];
            line = line.trim();
            line = processBold(line);
            if (line.startsWith("####")) {
                lastIsPlain = false;
                buf.append("<h4>");
                line = line.substring(4).trim();
                buf.append(line);
                buf.append("</h4>");
            }
            else if (line.startsWith("###")) {
                lastIsPlain = false;
                buf.append("<h3>");
                line = line.substring(3).trim();
                buf.append(line);
                buf.append("</h3>");
            }
            else if (line.startsWith("##")) {
                lastIsPlain = false;
                buf.append("<h2>");
                line = line.substring(2).trim();
                buf.append(line);
                buf.append("</h2>");
            }
            else if (line.startsWith("#")) {
                lastIsPlain = false;
                buf.append("<h1>");
                line = line.substring(1).trim();
                buf.append(line);
                buf.append("</h1>");
            }
            else if (line.startsWith("* ")) {
                lastIsPlain = false;
                buf.append("<ul>");
                while (line.length() > 0 && line.startsWith("* ")) {
                    line = line.substring(2).trim();
                    buf.append("<li>").append(line).append("</li>");
                    i++;
                    if (i >= lines.length) {
                        break;
                    } else {
                        line = lines[i];
                        line = line.trim();
                    }
                }
                i--;
                buf.append("</ul>");
            }
            else if (line.startsWith("1.")) {
                lastIsPlain = false;
                buf.append("<ol>");
                while (line.length() > 0 && Character.isDigit(line.charAt(0))) {
                    line = line.substring(2).trim();
                    buf.append("<li>").append(line).append("</li>");
                    i++;
                    if (i >= lines.length) {
                        break;
                    } else {
                        line = lines[i];
                        line = line.trim();
                    }
                }
                i--;
                buf.append("</ol>");
            } else {
                if (lastIsPlain && lastLine.equals("") && !line.equals("")) {
                    buf.append("<p>");
                    buf.append(line.trim()).append(' ');
                }
                else if (lastIsPlain && !lastLine.equals("") && !line.equals("")) {
                    buf.append(line.trim()).append(' ');
                }
                else if (lastIsPlain && lastLine.equals("") && line.equals("")) {

                }
                else if (!lastIsPlain) {
                    lastIsPlain = true;
                    buf.append(line.trim()).append(' ');
                }
                lastLine = line;

//                if (!lastIsPlain
//                        || lastLine.equals(""))
//                    buf.append("<p>\n");
//                lastIsPlain = true;
//                buf.append(line).append("\n");
//                lastLine = line;
            }
        }

        String result = buf.toString();
        return result;
    }

    static private String processBold(String line) {
        if (line.contains("**")) {
            int start = line.indexOf("**");
            int end = line.indexOf("**", start+2);
            if (end == -1) return line;  // malformed
            String before = line.substring(0, start);
            String after = line.substring(end+2);
            String bold = line.substring(start+2, end);

            StringBuilder buf = new StringBuilder();
            buf.append(before);
            buf.append("<span style=\"font-weight:bold\">");
            buf.append(bold);
            buf.append("</span>");
            int newFrom = buf.length();
            buf.append(processBold(after));
            return buf.toString();
        }
        return line;
    }
}
