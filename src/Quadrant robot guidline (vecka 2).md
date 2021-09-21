Förslag rörselser
(vecka 2), Filip

Robot åker till kanten
1. Start: Robot startar i en av fyra kvadranter (kl. 1 / 5 / 7 / 11) eller i mittpunkten.
      a.	Specialfall (Om robot startar i mittpunkten rör den sig rakt upp)

2. Gå mot vågrät vägg: Robot rör sig mot övre (norr) eller nedre vägg (syd) beroende på vilken som är närmast.
      b.	(skjut på robot som står i vägen, skottstorlek 3)
      

Robot åker fram och tillbaka (i X-led)
3. Gå i X-led: Robot åker till mitten av aktuell kvadrant,


4.	Loopa beteende: Robot rör sig 100 pixel höger, 100 pixel vänster

Robot skjuter
5. Robot skott: Robot skjuter mot närmsta fiende
      a.	(om avstånd < 200 pixels, skottstorlek 3)
      b.	(om avstånd > 200 pixels & om 6-10 robotar skottstorlek 2)
      c.	(om avstånd 200-350 pixels & 1-5 robotar, skottstorlek 2)
      d.	(om avstånd > 350 pixels & om 1-5 robotar skottstorlek 1)
      e.	Prioriterat (överstyr): Robot prioriterar fiender på avstånd < 150 pixels, 
5.e forts. när robot står vid vågrätt vägg och vinkel mellan robot och fiende är mindre än 26°, multiplicera fiendens avstånd med 0.25. Skjut med skottstorlek 3. 

6. Om fiende dör, gå tillbaka till #3
   


Sammanfattningsvis
Rörelse (Haris): Se punkt 1-4 & 5e
Radar (Albin): Se punkt 5
Kanon (Karl): Se punkt 5
