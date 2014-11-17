package jp.massbank.spectrumsearch.db;

public class Instrument {
  int no;
  String type;
  String name;
  @Override
  public String toString() {
    return "Instrument [no=" + no + ", type=" + type + ", name=" + name + "]";
  }
  public int getNo() {
    return no;
  }
  public String getType() {
    return type;
  }
  public String getName() {
    return name;
  }
}
