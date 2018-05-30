package com.example.trabajo.myapplication;

import java.io.*;
import java.util.*;

class MyServer extends NanoHTTPD {
  public MyServer() throws IOException {
    super(44444, null);
  }
  public Response serve(String uri, String meth, Properties header, Properties params, Properties files) {
    System.out.println(uri);
    if (uri.equals("/")) {
      String str = "<html><head><title>hoge</title></head><body><a href=\"/test\">test</a></body></html>";
      return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, str);
    } else if (uri.equals("/test")) {
      try {
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        Thread t = new MyThread(pos);
        t.start();
        //pos.write("HELLO WORLD PIPE TEST".getBytes());
        //pos.close();
        NanoHTTPD.Response res = new NanoHTTPD.Response(HTTP_OK, MIME_PLAINTEXT, pis);
        res.addHeader("Transfer-Encoding", "chunked");
        return res;
        //return new NanoHTTPD.Response(HTTP_OK, MIME_PLAINTEXT, pis);
      } catch (IOException e) {
        e.printStackTrace();
        return new NanoHTTPD.Response(HTTP_INTERNALERROR, MIME_PLAINTEXT, "Internal Server Error");
      }
    } else {
      return new NanoHTTPD.Response(HTTP_NOTFOUND, MIME_PLAINTEXT, "404 File Not Found");
    }
  }
  public static void main(String[] args) {
    try {
      new MyServer();
      System.in.read();
    } catch (IOException e) {
      System.out.println("ERROR WORLD!");
      return;
    }
  }
  private class MyThread extends Thread {
    PipedOutputStream pos;
    public MyThread(PipedOutputStream s) {
      pos = s;
    }
    public void run() {
      try {
        pos.write("5\r\nhoge \r\n".getBytes());
        pos.flush();
        Thread.sleep(500);
        pos.write("4\r\nhoge\r\n0\r\n\r\n".getBytes());
        pos.close();
      } catch (IOException e) {
        return;
      } catch (InterruptedException e) {
        return;
      }
    }
  }
}