
   var i=0;
   var timeout;
   var hochimin;
   window.onload=function(){
      start=document.getElementById("startButton");
      stop=document.getElementById("stopButton");
      hochimin=new Array();
      var jpegs=["DSCN1211.jpg", "DSCN1212.jpg","DSCN1239.jpg",     
                "DSCN1257.jpg", "DSCN1202.jpg", "DSCN1264.jpg",
                "DSCN1218.jpg", "DSCN1266.jpg","DSCN1200.jpg", 
                 "DSCN1232.jpg"];  
      var picfile="file:///C:/wamp/www/images/";  
      var title = [ "Sidewalk food of noodles, chicken, fish, etc.",
                  "Balloon lady in Hochiminh City",
                  "Water buffalo pops his head up from the mud.",
                  "Sundown along the Mekong Delta", 
                  "Some very colorful and hairy fruit",
                  "Meat vendor in Ben Hanh market",
                  "Single proprietor selling goods",
                  "Preparing food at dawn",
                  "No thanks. No Moped. We're walking",
                  "Tour boats in the Mekong Delta"
                  ];
      for(var i=0; i<jpegs.length;i++){  //Preload images
         hochimin[i]=new Image();
         hochimin[i].src=picfile + jpegs[i];
         hochimin[i].title=title[i];  // Assign title to each image
        }   
      if(window.attachEvent){ // Is this Microsoft IE?
          start.attachEvent("onclick", startSlideShow);
          stop.attachEvent("onclick", stopSlideShow);
        }
     else{ // W3C standard
         start.addEventListener("click", startSlideShow, false);
         stop.addEventListener("click", stopSlideShow, false);
        }
    }
    function startSlideShow(){
       picture=document.getElementsByTagName("img").item(0);
       divCaption=document.getElementById("description");
       if(i < hochimin.length){
          	picture.src=hochimin[i].src; //Image replacement
          	divCaption.innerHTML=hochimin[i].title
         	i++;
       } 
 	else{i=0;}
         	timeout = setTimeout('startSlideShow()',5000);
     }
     function stopSlideShow(){
       		clearTimeout(timeout);
     } 