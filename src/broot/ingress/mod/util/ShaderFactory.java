package broot.ingress.mod.util;

import android.util.Log;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ShaderFactory {

  private String line;
  private LinkedList<String> lines;
  private ListIterator<String> cursor;

  private ShaderFactory(String source) {
    lines = new LinkedList<String>(Arrays.asList(source.replace("\\n", "\n").split("\n|\\n")));
    cursor = lines.listIterator();
  }

  public static ShaderFactory start(String source) {
    return new ShaderFactory(source);
  }

  public ShaderFactory findLine(String regex) throws NoSuchElementException {
    cursor = lines.listIterator();
    return findLineDown(regex);
  }

  public ShaderFactory findLineDown(String regex) throws NoSuchElementException {

    for (line = cursor.next(); cursor.hasNext(); line = cursor.next()) {
      if (line.matches(regex)) {
        return this;
      }
    }
    throw new NoSuchElementException(regex);
  }

  public ShaderFactory findLineUp(String regex) throws NoSuchElementException {

    for (line = cursor.previous(); cursor.hasPrevious(); line = cursor.previous()) {
      if (line.matches(regex)) {
        return this;
      }
    }
    throw new NoSuchElementException(regex);
  }

  public ShaderFactory replaceLine(String newLine) {
    cursor.set(newLine);
    return this;
  }

  public ShaderFactory replace(String target, String replace) {
    line = line.replace(target, replace);
    cursor.set(line);
    return this;
  }

  public ShaderFactory commentLine() {
    line = "//" + line;
    cursor.set(line);
    return this;
  }

  public ShaderFactory removeLine() {
    line = null;
    cursor.remove();
    return this;
  }

  public ShaderFactory nextLine() {
    line = cursor.next();
    return this;
  }

  public String commit() {
    final StringBuilder builder = new StringBuilder();
    for (String str : lines) {
      builder.append(str);
      builder.append('\n');
    }
    return builder.toString();
  }
}
