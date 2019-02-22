/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mba;


import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.imageio.ImageIO;


/**
 *
 * @author JeffRolan11
 */
public class FXMLDocumentController implements Initializable { 
    
    //To Strip the extensions from the files
    static String stripExtension(String str){
        if(str == null) return null;
        int pos = str.lastIndexOf(".");
        if (pos == -1) return str;
        return str.substring(0,pos);
    }
    
    private MediaPlayer mediaPlayer;
    private int relaxedNum, distractedNum, boredNum, curiousNum, frustratedNum, othersNum, lastStop;
    private String capture;
    private Duration duration;
   
    @FXML
    private MediaView mediaView;
    private String filePath;
    
    @FXML 
    private Slider seekSlider;
    
    @FXML
    private Label movieName;
    
    @FXML 
    private Button btnNew;
    
    @FXML
    private Button relaxed;
    
    @FXML
    private Button distracted;
    
    @FXML
    private Button bored;
    
    @FXML
    private Button curious;
    
    @FXML 
    private Button others;
    
    @FXML
    private Button frustrated;
    
    @FXML
    private Label playTime;
    
    @FXML
    private Label playDuration;
    
    @FXML
    private Button rewindButton;
    
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        //Code for choosing files from a directory
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select a File ('.mp4','.avi','.flv','.wmv','.mov')", "*.mp4","*.avi","*.flv","*.wmv","*.mov");
        
                fileChooser.getExtensionFilters().add(filter);
                File file = fileChooser.showOpenDialog(null);
                String movie = file.getName();
                filePath = file.toURI().toString();
                rewindButton.setDisable(true);
                
                //creating the main folder
                File firstFile = new File("C:\\FINALDATA");
                if(!firstFile.exists()){
                    boolean result = firstFile.mkdir();
                    System.out.print("Yes!");
                }else{
                    System.out.print("Nope");
                }    
                
                //Setting the date and time for directory naming
                String s;
                Format formatter;
                Date date = new Date();
                
                formatter = new SimpleDateFormat("MM-dd-YYYY hh-mm-ss a");
                s = formatter.format(date);
                
                //implementing and concatenationg of varibales to be used as folder name
                String finalMovie = stripExtension(movie);
                String PATH = "C:\\FINALDATA\\";
                String name = finalMovie+" ("+s+")";
                   
                String folderName = PATH.concat(name);
                
		File fileName = new File(folderName);		
                
                //statement for checking and testing purposes only
		if(!fileName.exists()){
			boolean result = fileName.mkdir();
                        System.out.print("Created!");
                }else{
                    System.out.print("Error!");
                }
               
                //implementing the video to media player from directory
                if(filePath != null){
                    movieName.setText(movie);
                    btnNew.setDisable(true);
                    Media media = new Media(filePath);
                    mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    
                    
                    
                  //mediaDuration
                  
                   mediaPlayer.setOnReady(new Runnable() {

                   @Override
                   public void run() {
                   String durStr = String.valueOf(media.getDuration().toMillis());
                   durStr = durStr.substring(0, durStr.indexOf("."));
                 
                   long miles = Long.valueOf(durStr);
                    String hums = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(miles),
                   TimeUnit.MILLISECONDS.toMinutes(miles) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(miles)),
                   TimeUnit.MILLISECONDS.toSeconds(miles) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(miles)));
                   playDuration.setText(hums);
        }
    });
                           
                 //adding seek slider for users ease of use and duration
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
               seekSlider.setMin(0.0);
               seekSlider.setValue(0.0);
               seekSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
               seekSlider.setValue(newValue.toSeconds());
                String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                curTime = curTime.substring(0, curTime.indexOf("."));
                    
                    //displaying the current time
                    long millis = Long.valueOf(curTime);
                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                   TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                   TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                   playTime.setText(hms);
                   
                   //auto pause every 15 seconds
                   int value = (int) newValue.toSeconds();
                   if(lastStop != value && value % 15 == 0){
                       lastStop = value;
                       mediaPlayer.pause();
                       rewindButton.setDisable(false);
                   }
                
            }       
                });
            
                seekSlider.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event) {
                         mediaPlayer.seek(Duration.seconds(seekSlider.getValue()));
                         Duration current = mediaPlayer.getCurrentTime();
                        }
                });
                rewindButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event){
                            double rewindTime = mediaPlayer.getCurrentTime().toSeconds() - 15;
                            mediaPlayer.seek(Duration.seconds(rewindTime));
                            Duration current = mediaPlayer.getCurrentTime();
                            mediaPlayer.pause();
                        }
                });
                    
                   
                //RELAXED
                relaxed.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event) {
                              WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                  Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (RELAXED)";
                                
                                 File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+relaxedNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"RELAXED",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+relaxedNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"RELAXED",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             relaxedNum++;
                        }     
                
                });
                
                //for shortcut key 
                relaxed.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), () ->{
                WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                  Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (RELAXED)";
                                
                                 File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+relaxedNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"RELAXED",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+relaxedNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"RELAXED",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             relaxedNum++;
                });
                
              
              //DISTRACTED
              distracted.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event) {
                              WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                   Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (DISTRACTED)";
                                
                                File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+distractedNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"DISTRACTED",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+distractedNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"DISTRACTED",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             distractedNum++;
                        }     
                
                });
              
                //for shortcut key
                distracted.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN), () ->{
                    WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                   Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (DISTRACTED)";
                                
                                File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+distractedNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"DISTRACTED",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+distractedNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"DISTRACTED",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             distractedNum++;
                });
                
                //BORED
                 bored.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event) {
                              WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                    Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (BORED)";
                                 File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+boredNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"BORED",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+boredNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"BORED",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             boredNum++;
                        }     
                
                });
                 
                //for shortcut key
                   bored.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN), () ->{
                        WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                        Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (BORED)";
                                 File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+boredNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"BORED",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+boredNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"BORED",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             boredNum++;
                });
                
                curious.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event) {
                              WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                  Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (CURIOUS)";
                                
                                File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+curiousNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"CURIOUS",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+curiousNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"CURIOUS",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             curiousNum++;
                        }     
                
                });
                
                //for shortcut key
                curious.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN), () ->{
                 WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                              Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (CURIOUS)";
                                
                                File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+curiousNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"CURIOUS",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+curiousNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"CURIOUS",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
       
                             curiousNum++;
                });
                
                //FRUSTRATED
                frustrated.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event) {
                              WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                  Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (FRUSTRATED)";
                                
                                File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+frustratedNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"FRUSTRATED",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+frustratedNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"FRUSTRATED",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             frustratedNum++;
                        }     
                
                });
                
                //for shortcut key
                frustrated.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), () ->{
                WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                    Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (FRUSTRATED)";
                                
                                File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+frustratedNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"FRUSTRATED",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+frustratedNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"FRUSTRATED",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             frustratedNum++;    
                    
                });
                
                  //FRUSTRATED
                others.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event) {
                              WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                  Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (OTHERS)";
                                
                                File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+othersNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"OTHERS",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+othersNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"OTHERS",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             othersNum++;
                        }     
                
                });
                
                //for shortcut key
                others.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), () ->{
                WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
                                  BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
                                    Writer writer = null;
                            
                            //for saving the image from media player captured by snapshot
                            try {
                                capture = folderName+"\\"+finalMovie+" (OTHERS)";
                                
                                File captureName = new File(capture);
                                 String curTime = String.valueOf(mediaPlayer.getCurrentTime());
                                 curTime = curTime.substring(0, curTime.indexOf("."));
                         
                                 long millis = Long.valueOf(curTime);
                                 String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                 
                                 long mill = Long.valueOf(curTime) - 15000;
                                 String startHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mill),
                                 TimeUnit.MILLISECONDS.toMinutes(mill) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mill)),
                                 TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                                 
                                 String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                 TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                 TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                
                                
                                if(!captureName.exists()){
                                    boolean result = captureName.mkdir();
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+othersNum+") "+hms+".png"));
                                System.out.print("Success!");
                                setRecord(startHms,endHms,"OTHERS",folderName+"\\Record.csv");
                                rewindButton.setDisable(true);
                                mediaPlayer.play();
                                }else{
                                ImageIO.write(bufImg, "png", new File(capture+"\\"+finalMovie+" ("+othersNum+") "+hms+".png"));
                                 System.out.print("Wow");
                                 setRecord(startHms,endHms,"OTHERS",folderName+"\\Record.csv");
                                 rewindButton.setDisable(true);
                                 mediaPlayer.play();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
       
                             othersNum++;    
                    
                });
                
         mediaPlayer.play();     
                }
    }
                                
    @FXML
    private void pauseVideo(ActionEvent event){
         mediaPlayer.pause();
         rewindButton.setDisable(false);
    }
     @FXML
    private void playVideo(ActionEvent event){
        mediaPlayer.play();
        rewindButton.setDisable(true);
    }
     @FXML
    private void stopVideo(ActionEvent event){
        mediaPlayer.stop();
        mediaPlayer.dispose();
        btnNew.setDisable(false);
        playDuration.setText("00:00:00");
    }
    @FXML
    private void exit(ActionEvent event){
        System.exit(0);
    }
    
            private void setRecord(String firstTime, String lastTime, String affect, String filepath){
                         try{
                         File fila = new File(filepath);
                         
                         if(!fila.exists()){
                            FileWriter fw = new FileWriter(fila, true);
                              BufferedWriter bw = new BufferedWriter(fw);
                              PrintWriter pw = new PrintWriter(bw);
                                 pw.println("START TIME , END TIME , AFFECT");
                                 pw.println(firstTime + "," + lastTime + "," + affect);  
                                 pw.flush();
                                 pw.close();
                         }else{
                             FileWriter fw = new FileWriter(fila, true);
                              BufferedWriter bw = new BufferedWriter(fw);
                              PrintWriter pw = new PrintWriter(bw);
                                pw.println(firstTime + "," + lastTime + "," + affect);  
                                pw.flush();
                                pw.close();
                         }
                        
                         }catch(Exception e){
                         e.printStackTrace();
                         }
                         }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
}
