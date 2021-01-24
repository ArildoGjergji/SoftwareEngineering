class Server{

  private String inet = "";
  private String inet6 = "";
  private String tcpPort = "";
  private String protocol = "";
  private String username = "";
  private String password = "";

  Server() {
    super();
  }

  Server(String inet, String inet6, String tcpPort, String protocol, String username,
  String password) {
    this.inet = inet;
    this.inet6 = inet6;
    this.tcpPort = tcpPort;
    this.protocol = protocol;
    this.username = username;
    this.password = password;
  }

  public String getInet() {
    return inet;
  }

  public String getInet6() {
    return inet6;
  }

  public String getTcpPort() {
    return tcpPort;
  }

  public String getProtocol() {
    return protocol;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  @Override public String toString() {
    return "{  inet: " + this.inet + ", inet6: " + this.inet6 + ", username: " + this.username
    + ", password: " + this.password + " }";
  }
}
