import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.lang.Object;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.Color;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


fun main(args: Array<String>) {
  val server: HttpServer = HttpServer.create(InetSocketAddress(8080), 0);
  server.createContext("/ejercicio1", ejercicio1());
  server.createContext("/ejercicio2", ejercicio2());
  server.createContext("/ejercicio3", ejercicio3());
  server.createContext("/ejercicio4", ejercicio4());
  server.setExecutor(null); // creates a default executor
  server.start();
}

class ejercicio1 : HttpHandler {
  override fun handle(t: HttpExchange) {
    if (t.getRequestMethod() == "POST") {
      val os: OutputStream = t.getResponseBody();  
      var response: ByteArray = t.getRequestBody().readBytes();
      val test: String = String(response);
      val idk = test.split("\"");
      val origen = idk[3].replace(" ", "+");
      val destino = idk[7].replace(" ", "+");
      val request_url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origen + "&destination=" + destino + "&key=AIzaSyAzzrnc71pLvEvOdY322DQwwbUsFQZT7Vg"
      println(origen);
      println(destino);
      val url = URL(request_url);
      val br = BufferedReader(InputStreamReader(url.openStream()));
      var maps: String = "";
      var temp: String = String();
      while(br.ready()){
        temp = br.readLine();
        println(temp);
        maps = maps + temp;
      }
      var splitted = maps.split("\"steps\" : [", "],               \"traffic_speed_entry\"");
      splitted = splitted[1].split("\"start_location\" : ", "\"end_location\" : ", ",                     \"html_instructions\"", ",                     \"travel_mode\"")
      var steps: MutableList<String> = ArrayList();
      var c = 0;
      var x = 0;
      while(c < splitted.size){
        if(c % 2 == 1){
            steps.add(splitted[c]);
            x++;
            println(splitted[c]);
        }
        c++;
      }
      c = 3;
      var json: String = String();
      if(steps.size == 1){
          json = "{\"ruta\":["  + steps[1] + "]}";
      }
      else if(steps.size == 2){
          json = "{\"ruta\":["  + steps[1] + ", " + steps[0] + "]}";
      }
      else if(steps.size == 2){
          json = "{\"ruta\":["  + steps[1] + ", " + steps[0] + ", " + steps[2] + "]}";
      }
      else{
          json = "{\"ruta\":[" + steps[1] + ", " + steps[0] + ", " + steps[2] + ", ";
          while(c < steps.size ){
              if(c % 2 == 0){
                  json = json + steps[c] + ", ";
              }
              c++;
          }
      }
      json = json.subSequence(0, json.length - 2).toString();
      json = json + "]}";
        
      println(json);
      response = json.toByteArray();
      t.getResponseHeaders().add("content-type", "json");
      t.sendResponseHeaders(200, response.size.toLong());
      os.write(response);
      os.close();
    }
  }
}

class ejercicio2 : HttpHandler {
  override fun handle(t: HttpExchange) {
    if (t.getRequestMethod() == "POST") {
      val os: OutputStream = t.getResponseBody();
      var response: ByteArray = t.getRequestBody().readBytes();
      val test: String = String(response);
      var idk = test.split("\"");
      val origen = idk[3].replace(" ", "+");
      var request_url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + origen + "&key=AIzaSyDlWabEzv6sC9AW1F_C1rc_nOz9o2nm0Bg";
      var url = URL(request_url);
      var br = BufferedReader(InputStreamReader(url.openStream()));
      var maps: String = "";
      var temp: String = String();
      while(br.ready()){
        temp = br.readLine();
        println(temp);
        maps = maps + temp;
      }
      var splitted = maps.split("\"location\" : {", "},            \"location_type\"");
      splitted = splitted[1].split("\"lat\" : ", "\"lng\" : ", ",", " ");
      var lat = splitted[16];
      var lon = splitted[33];
      request_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lon + "&radius=500&type=restaurant&key=AIzaSyAp0wmWixdzDo3MBI7TIY1XN4okirXUeYM";
      url = URL(request_url);
      br = BufferedReader(InputStreamReader(url.openStream()));
      maps = "";
      while(br.ready()){
        temp = br.readLine();
        println(temp);
        maps = maps + temp;
      }
      splitted = maps.split("\"location\" : {", "},            \"viewport\"", "\"name\" :");
      var c = 0;
      var x = 0;
      var steps: MutableList<String> = ArrayList();
      while(c < splitted.size){
        if(c % 3 == 1){
          steps.add(splitted[c]);
          x++;
        }
        if(c % 3 == 0 && c != 0){
            idk = splitted[c].split("         \"");
            steps.add(idk[0]);
            x++;
        }
        c++;
      }
      var json: String = String();
      c = 0;
      json = "{\"restaurantes\":[";
      while(c < steps.size/2){
        json = json + "{\"nombre\":" + steps[c * 2 + 1] + steps[c * 2] + "}, ";
        c++;
      }
      json = json.subSequence(0, json.length - 2).toString();
      json = json + "]}";
      println(json);
      response = json.toByteArray();
      t.getResponseHeaders().add("content-type", "json");
      t.sendResponseHeaders(200, response.size.toLong());
      os.write(response);
      os.close();
    }
  }
}

class ejercicio3 : HttpHandler {
  override fun handle(t: HttpExchange) {
    if (t.getRequestMethod() == "POST") {
      val os: OutputStream = t.getResponseBody();
      var response: ByteArray = t.getRequestBody().readBytes();
      val test: String = String(response);
      var idk = test.split("\"");
      val nombre = idk[3];
      var img_data = idk[7];
      var gray_img = "";
      try{
        var img: ByteArray = Base64.getDecoder().decode(img_data);
        var bais: ByteArrayInputStream = ByteArrayInputStream(img);
        var editable_img: BufferedImage = ImageIO.read(bais);
        println(editable_img.getHeight());
        println(editable_img.getWidth());
        for(x in 0..editable_img.getWidth() - 1){
          for(y in 0..editable_img.getHeight() - 1){
            var rgb = editable_img.getRGB(x, y);
            var r = (rgb shr 16) and 0xFF;
            var g = (rgb shr 8) and 0xFF;
            var b = (rgb and 0xFF);
            //println("old");
            //println(rgb);

            var grayLevel = (0.21 * r + 0.72 * g + 0.07 * b).toInt();
            var gray = grayLevel shl 16 or (grayLevel shl 8) or grayLevel;
            //println("new");
            //println(gray);
            editable_img.setRGB(x, y, gray);
          }
        }
        //var new_img:ByteArray = (editable_img.getData().getDataBuffer() as DataBufferByte).getData();
        var baos: ByteArrayOutputStream = ByteArrayOutputStream();
        ImageIO.write(editable_img, "bmp", baos);
        var new_img: ByteArray = baos.toByteArray();
        gray_img = Base64.getEncoder().encodeToString(new_img);
      }catch(e: IllegalArgumentException){
          var err = "{\"error\": \"Intente de nuevo\"}";
          response = err.toByteArray();
          t.getResponseHeaders().add("content-type", "json");
          t.sendResponseHeaders(500, response.size.toLong());
          os.write(response);
          os.close();
      }
      var json: String = String();
      var name = nombre.split(".");
      json = "{\"nombre\":\"" + name[0] + "(blanco y negro)." + name[1] + "\", \"data\": \"" + gray_img + "\"}";
      response = json.toByteArray();
      println(json);
      t.getResponseHeaders().add("content-type", "json");
      t.sendResponseHeaders(200, response.size.toLong());
      os.write(response);
      os.close();
    }
  }
}

class ejercicio4 : HttpHandler {
  override fun handle(t: HttpExchange) {
    if (t.getRequestMethod() == "POST") {
      val os: OutputStream = t.getResponseBody();
      var response: ByteArray = t.getRequestBody().readBytes();
      val test: String = String(response);
      var idk = test.split("\"");
      val nombre = idk[3];
      var img_data = idk[7];
      var alto_temp = idk[12].split(": ", ",");
      var ancho_temp = idk[14].split(": ", "\n");
      var alto = alto_temp[1].toInt();
      ancho_temp = ancho_temp[1].split(" ");
      var ancho= ancho_temp[0].subSequence(0, ancho_temp[0].length - 1).toString().toInt();

      println(nombre);
      println(img_data);
      println(alto);
      println(ancho);
      
      var small_img = "";
      try{
        var img: ByteArray = Base64.getDecoder().decode(img_data);
        var bais: ByteArrayInputStream = ByteArrayInputStream(img);
        var editable_img: BufferedImage = ImageIO.read(bais);
        var smaller_img: BufferedImage = BufferedImage(ancho, alto, 1);
        
        var height = editable_img.getHeight();
        var width = editable_img.getWidth();
        
        var DivX = width.toFloat()/ancho.toFloat();
        var DivY = height.toFloat()/alto.toFloat();

        var ResizedWidth = (width/DivX).toInt();
        var ResizedHeight = (height/DivY).toInt();
        println("" + width + " / " + ancho + " = " + DivX);
        println("" + height + " / " + alto + " = " + DivY);

        for(x in 0..ResizedWidth - 1){
          for(y in 0..ResizedHeight - 1){
            var pixel = editable_img.getRGB((x * DivX).toInt(), (y * DivY).toInt());
            smaller_img.setRGB(x, y, pixel);
          }
        }
        var baos: ByteArrayOutputStream = ByteArrayOutputStream();
        ImageIO.write(smaller_img, "bmp", baos);
        var new_img: ByteArray = baos.toByteArray();
        small_img = Base64.getEncoder().encodeToString(new_img);
      }catch(e: IllegalArgumentException){
          var err = "{\"error\": \"Intente de nuevo\"}";
          response = err.toByteArray();
          t.getResponseHeaders().add("content-type", "json");
          t.sendResponseHeaders(500, response.size.toLong());
          os.write(response);
          os.close();
      }
      var json: String = String();
      var name = nombre.split(".");
      json = "{\"nombre\":\"" + name[0] + "(reducido)." + name[1] + "\", \"data\": \"" + small_img + "\"}";
      response = json.toByteArray();
      println(json);
      t.getResponseHeaders().add("content-type", "json");
      t.sendResponseHeaders(200, response.size.toLong());
      os.write(response);
      os.close();
    }
  }
}
