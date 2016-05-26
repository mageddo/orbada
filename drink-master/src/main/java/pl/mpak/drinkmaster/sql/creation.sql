create table drinks (
  dnk_id varchar(40) not null,
  dnk_name varchar(100) not null,
  dnk_make_up varchar(4000),
  dnk_description varchar(4000)
)
/
alter table drinks add constraint drink_pk primary key (dnk_id)
/
create table drink_compos (
  dnc_id varchar(40) not null,
  dnc_dnk_id varchar(40) not null,
  dnc_no integer not null,
  dnc_component varchar(200) not null
)
/
alter table drink_compos add constraint drink_compo_pk primary key (dnc_id)
/
alter table drink_compos add constraint drink_compo_dnk_fk foreign key (dnc_dnk_id) references drinks (dnk_id) on delete cascade
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130163841-00000D71B6F97B85-E30A787E', 'Bernardo', 'W shakerze umieœciæ lód dodaæ d¿in, triple sec oraz sok z cytryny. Mieszaæ przez ok. 5 sekund i odcedzaj¹c przelaæ do kieliszków koktajlowych. Aby pobudziæ aromat aromatyzowaæ skórk¹ z cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130163926-00000D7C44426C69-4E785953', 'Bitter Gimlet', 'W szklanicy barmañskiej umieœciæ limê i wycisn¹æ z niej sok. Uzupe³niæ d¿inem, angostur¹ oraz lodem. Wszystko ze sob¹ wymieszaæ i przecedziæ do szklanki typu oldfashioned. Na koniec dekorowaæ plasterkiem limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130163957-00000D8377A3C1A6-E6B5D6F9', 'Bloodhound', 'W umieœciæ wszystko co zosta³o wymienione w sk³adnikach. Zmiksowaæ ze sob¹ sk³adniki i przecedziæ do kieliszka koktajlowego. Na koniec wykonaæ dekoracje z œwie¿utkiej truskawki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164022-00000D892BA695BA-9CA5A79F', 'Blue Star', 'W umieœciæ wszystko w shakerze w 1/2 wype³nionego lodem. Mieszaæ przez 5 sekund i przecedziæ do szk³a koktajlowego wczeœniej w 1/3 wype³nionej lodem.. Na koniec wykonaæ dekoracje z plasterka pomarañczy (po³ówka).', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164045-00000D8EA0BEF9DB-33A4A228', 'Bridesmaid', 'W szklance typu highball wczeœniej wype³nionej t³uczonym lodem umieœciæ d¿in, syrop cukrowy, sok z cytryny oraz angosturê. Wymieszaæ, a nastêpnie dodaæ piwo imbirowe. Na koniec wykonaæ dekoracje z cytryny oraz spiralkê ze skórki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164106-00000D937A7D98DF-D00F6230', 'Bronx', 'W shakerze umieœciæ lód dodaæ d¿in, wermut rosso oraz wytrawny. Mieszaæ przez ok. 5 sekund i odcedzaj¹c przelaæ do kieliszków koktajlowych.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164129-00000D98E0E34F8B-367A0FB1', 'Capitan''s Table', 'W shakerze umieœciæ lód dodaæ d¿in, Campari, grenadynê sok pomarañczowy. Mieszaæ przez ok. 5 sekund i odcedzaj¹c przelaæ do szklanki highball w 3/4 wype³nionej lodem. Jako ostatni dodaæ ginger ale.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164151-00000D9DDE36DD5D-9E8F227F', 'Carla', 'Do shakera wype³nionego lodem wlaæ d¿in, sok z marakui oraz pomarañczowy. Wymieszaæ wszystko i przelaæ do szklanki typu highball razem z lodem z shakera. Uzupe³niæ lemoniad¹. Na koniec wykonaæ dekoracje z plasterka pomarañczy lub jego skórki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164213-00000DA30804B327-FE548BA8', 'Caruso', 'Do shakera wype³nionego lodem wlaæ sk³adniki i dobrze ze sob¹ wymieszaæ. Nastêpnie przecedziæ do kieliszka koktajlowego. Caruso jest gotowy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164243-00000DAA0997DD52-91631A2E', 'Cesar Ritz', 'W szklanicy barmañskiej wype³nionej lodem wlaæ sk³adniki i wymieszaæ ze sob¹. Nastêpnie przecedziæ do kieliszka koktajlowego. Na koniec dekorowaæ wisienk¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164303-00000DAEAF8C100E-45386D4D', 'Cloister', 'W shakerze wype³nionym lodem wlaæ sk³adniki i wymieszaæ ze sob¹. Mieszaæ 5 sekund. Przecedziæ do kieliszka koktajlowego. Na koniec dekorowaæ grejpfrutem pasek ze skórki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164325-00000DB3ECABD835-FBC00564', 'Clover Club', 'Do shakera wype³nionego 3/4 lodem wlaæ sk³adniki i wymieszaæ ze sob¹. Mieszaæ oko³o 20 sekund. Przecedziæ do kieliszka koktajlowego. Na koniec dekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164342-00000DB7DBFD8B39-7584D27B', 'Crimson Sunset', 'W shakerze umieœciæ lód dodaæ d¿in oraz sok cytrynowy. Mieszaæ przez ok. 5 sekund i odcedzaj¹c przelaæ do szklanki koktajlowej. Jako ostatnio dodaæ porto i grenadynê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164401-00000DBC41E541D1-21F94C3A', 'Collins', 'Wczeœniej zmro¿on¹ szklankê nape³niæ lodem w 3/4 objêtoœci. Dodaæ d¿in, syrop cukrowy, sok cytrynowy a na koniec wodê gazowan¹ dope³niæ do porz¹danej objêtoœci. Dekorowaæ plasterkiem cytryny oraz s³omk¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164431-00000DC345F51D29-5FEB4363', 'Delmonico', 'W shakerze umieœciæ lód dodaæ d¿in oraz sok cytrynowy. Mieszaæ przez ok. 5 sekund i odcedzaj¹c przelaæ do szklanki koktajlowej. Jako ostatnio dodaæ porto i grenadynê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164452-00000DC8228A7A6E-06C188D0', 'Dragonfly', 'Szklankê typu highball wype³niæ w 3/4 lodem. Dodaæ d¿in i ale imbirowy. Na koniec przybraæ skórk¹ cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164528-00000DD085C646F0-D109857A', 'Dundee', 'W shakerze wype³nionym lodem wlaæ sk³adniki i wymieszaæ ze sob¹. Mieszaæ oko³o 5 sekund. Przecedziæ do szklanki typu oldfashioned. Na koniec dekorowaæ plasterkiem cytryny (mo¿e byæ ta co przed chwil¹ zosta³a wyciœniêta).', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164544-00000DD44AD37127-8DC1C263', 'Fifty - Fifty', 'Do szklanicy barmañskiej wsypaæ 1/2 lodu. Wlaæ d¿in, wermut. Wszystko dobrze wymieszaæ i przelaæ do kieliszków koktajlowych.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164602-00000DD87D2AFEF1-2D29FB3B', 'Flying Dutchman', 'Szklankê typu old fashioned wype³niæ w 3/4 kostkami lodu. Do tak przygotowanej dodaæ d¿in oraz triple sec i dobrze wymieszaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164621-00000DDCC1A4BD61-B03A52CC', 'Arctic Summer', 'W szklance typu highball umieœciæ do 1/2 objêtoœci lód. Dodaæ d¿in, brandy oraz grenadynê. Na koniec uzupe³niæ gorzk¹ gazowan¹ lemoniad¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164654-00000DE471FD8595-5CC544BF', 'Barbarella', 'W shakerze umieœciæ 1/2 objêtoœci lód. Umieœciæ tam d¿in, Curacao, wermut oraz Galliano. Mieszaæ 5 sekund i przelaæ do szklanki typu oldfashioned wype³nionej do 1/2 objêtoœci t³uczonym lodem. Na koniec dekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164723-00000DEB468C2588-EF50677D', 'Bee''s Knees', 'W shakerze umieœciæ 1/2 objêtoœci lód. Wymieszaæ tam d¿in, sok i miód przez 5 sekund i przelaæ do szk³a koktajlowego. Zobacz jak¹ ciekaw¹ kompozycj¹ jest po³¹czenie miodu z cytryn¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130164744-00000DF009E2A6EC-19E2B84B', 'Bennet', 'W shakerze umieœciæ lód dodaæ d¿in, syrop cukrowy oraz sok z cytryny. Mieszaæ przez ok. 5 sekund i odcedzaj¹c przelaæ do kieliszków koktajlowych.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170148-00000EB48E3C4977-4728B367', 'Gentlemen''s Club', 'Szklankê typu oldfashioned wype³niæ w 3/4 kostkami lodu. Do tak przygotowanej dodaæ d¿in, brandy, s³odki wermut, wodê sodow¹ i dobrze wymieszaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170213-00000EBA63216D83-42207B53', 'Gibson', 'Do szklanicy barmañskiej wsypaæ 1/2 lodu. Wlaæ d¿in, wermut. Wszystko dobrze wymieszaæ i przelaæ do kieliszków koktajlowych. Wrzuciæ dwie cebulki koktajlowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170230-00000EBE60909B89-F2AF7797', 'Gimlet', 'Szklankê typu oldfashioned wype³niæ w 3/4 kostkami lodu. Do tak przygotowanej dodaæ d¿in i sok z limonki oraz dobrze wymieszaæ. Na koniec (opcjonalnie) dodaæ wody sodowej, a koniecznie kawa³ek limonki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170249-00000EC2D8E27999-482CB46F', 'Gin Cobbler', 'Na pocz¹tku nale¿y rozpuœciæ w osobnej szklance cukier w wodzie. Nastêpnie wype³niæ w 1/2 objêtoœci lodem szklankê typu highball, wlaæ d¿in oraz wczeœniej rozpuszczony cukier. Mieszaæ oko³o 5 sekund. Uzupe³niæ wod¹ gazowan¹. Na koniec dekorowaæ plasterkiem cytryny lub pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170308-00000EC75CBE8ADB-54877FA6', 'Gin Cocktail', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ d¿in i pomarañczówkê. Mieszaæ oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Na koniec - nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170325-00000ECB35DF7834-457ED762', 'Gin Daisy', 'W shakerze umieœciæ lód dodaæ d¿in, sok cytrynowy, cukier puder i grenadyna. Mieszaæ przez ok. 5 sekund i odcedzaj¹c przelaæ do szklanki typu oldfashioned w której wczeœniej umieœciliœmy t³uczony lód. Jako ostatnio dodaæ (opcjonalnie) wodê gazowan¹. Do dekoracji u¿ywamy: wisienkê koktajlow¹, plasterek pomarañczy oraz ga³¹zkê miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170400-00000ED35207A32B-F56FEB50', 'Gin Fizz', 'W shakerze umieœciæ lód dodaæ d¿in, sok cytrynowy, cukier puder. Mieszaæ przez ok. 5 sekund i odcedzaj¹c przelaæ do szklanki typu highball w której wczeœniej umieœciliœmy lód. Jako ostatnio dodaæ wodê sodow¹. Piæ niezw³ocznie po przygotowaniu kiedy jeszcze drink musuje.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170414-00000ED6A43C5B3C-FCFC5893', 'Gin Rickey', 'Szklankê typu highball wype³niæ w 1/2 kostkami lodu i wycisn¹æ sok z limonki. Do tak przygotowanej dodaæ d¿in i dobrze wymieszaæ. Na koniec przybraæ kawa³kiem limonki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170431-00000EDAB616B085-98287633', 'Gin Sling', 'W shakerze umieœciæ 1/2 objêtoœci lód dodaæ d¿in, syrop cukrowy, sok cytrynowy oraz wodê. Mieszaæ przez ok. 5 sekund i odcedzaj¹c przelaæ do szklanki typu highball w której wczeœniej umieœciliœmy 3/4 objêtoœci lodu. Aromatyzowaæ skórk¹ cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170450-00000EDF085B9AA2-1114C1CB', 'Gin Smash', 'Shaker w pierwszej kolejnoœci pos³u¿y do rozpuszczenia cukru. Nastêpnie w³o¿yæ miêtê i rozgnieœæ j¹. Po wykonaniu tych czynnoœci przyszed³ czas na wype³nienie shakera lodem w 1/2 objêtoœci i dodanie d¿inu. Mieszaæ oko³o 15 sekund. Przecedziæ do kieliszka od wina wczeœniej wype³nionego lodem. Na koniec dekorowaæ listkami miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170509-00000EE3634721B2-51D256D0', 'Gin Sour', 'Shaker wype³niæ lodem w 1/2 objêtoœci i dodaæ d¿inu, cukier oraz wycisn¹æ cytrynê. Mieszaæ energicznie przez oko³o 5 sekund. Przecedziæ do szklanki typu oldfashioned. Opcjonalnie mo¿na uzupe³niæ wod¹ gazowan¹. Na koniec w³o¿yæ s³omkê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170526-00000EE75F8B35AB-CDE419DB', 'Gin Swizzle', 'W shakerze umieœciæ 1/2 objêtoœci lód dodaæ d¿in, sok z limonki, cukier. Mieszaæ przez ok. 5 sekund i odcedzaj¹c przelaæ do szklanki typu Collins w której wczeœniej umieœciliœmy 3/4 objêtoœci lodu. Dodaæ wody gazowanej. Dekorowaæ s³omk¹ oraz mieszade³kiem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170543-00000EEB76D1B5EE-75FE9442', 'Grape Cocktail', 'Shaker wype³niæ lodem w 1/2 objêtoœci i dodaæ d¿inu, sok oraz likier. Mieszaæ energicznie przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Na koniec dekorowaæ wisienk¹ koktajlow¹ oraz spiralk¹ ze skórki grapefruita.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170600-00000EEF43F9853E-45509C06', 'Grape Skirt', 'W shakerze umieœciæ 1/2 objêtoœci lód dodaæ d¿in, triple sec, sok ananasowy i grenadyny. Mieszaæ przez ok. 5 sekund i przelaæ do szklanki typu oldfashioned. Dekorowaæ plasterkiem ananasa.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170615-00000EF2E5CDD75B-786BDC1E', 'Jockey Club', 'Shaker wype³niæ lodem w 1/2 objêtoœci i dodaæ wszystkie sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Na koniec dekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170632-00000EF6B4F2A4AA-AF3B5F5F', 'Honolulu', 'W shakerze umieœciæ 1/2 objêtoœci lód dodaæ d¿in, wszystkich soków oraz cukru. Mieszaæ przez ok. 5 sekund i przelaæ do kieliszka koktajlowego (wczeœniej sch³odzonego).', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170650-00000EFAFD28D8CE-2F0DB58E', 'Horse''s Neck', 'Szklankê typu highball wype³niæ w 1/2 objêtoœci lodem. Wlaæ d¿in, a resztê uzupe³niæ piwem imbirowym. Na koniec dekoracjê wykonaæ ze skórki cytryny spiralka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170713-00000F003F4E6D16-F0DF9164', 'Lady Killer', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ wszystkie wy¿ej wymienione sk³adniki. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do szklanki typu highball wczeœniej w ponad po³owie wype³nionej lodem. Na koniec dekoracjê wykonaæ z kawa³eczka ananasa.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170729-00000F040F67330D-CFB2ED2B', 'Leap Year', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ d¿in, wermut i Grand Marnier. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do kieliszka koktajlowego. Na koniec wycisn¹æ kilkanaœcie kropli cytryny. Przybraæ spiralk¹ ze skórki cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170756-00000F0A520F9ABA-22A70EB6', 'Luigi', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ d¿in, Cointreau, wermut oraz sok z mandarynki. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do kieliszka koktajlowego. Przybraæ spiralk¹ kawa³kiem mandarynki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170808-00000F0D06EDC754-D179C266', 'Martini', 'Do szk³a koktajlowego wlaæ d¿in i wermut. Na koniec wrzuciæ zielon¹ oliwkê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170828-00000F11DCA5B773-490F27C0', 'Maiden''s Prayer', 'W shakerze umieœciæ 1/2 objêtoœci lód dodaæ d¿in, sok z cytryny oraz triple sec. Mieszaæ przez ok. 5 sekund i przelaæ do kieliszka koktajlowego (wczeœniej sch³odzonego).', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170846-00000F15EA49E22F-70BDC140', 'Monkey Gland', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ d¿in grenadynê oraz sok pomarañczowy. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do kieliszka od wina. Dekorowaæ plasterkiem pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170904-00000F1A0F46816F-2866C6E3', 'My Fair Lady', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ wszystkie ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do kieliszka koktajlowego. Dekorowaæ spiralk¹ ze skórki pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170922-00000F1E4115DAF7-B0A35D64', 'Orange Blossom', 'W shakerze umieœciæ 1/2 objêtoœci lód dodaæ d¿in, sok pomarañczowy oraz cukier. Mieszaæ przez ok. 5 sekund i przelaæ do szklanki typu sour wczeœniej nape³nionego 3/4 lodem. Dekorujemy plasterkiem pomarañczy oraz wisienk¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130170942-00000F22FF23662E-51101D0B', 'Parisian', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ wszystkie ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171001-00000F2764EB904A-D346FE2C', 'Park Avenue', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ wszystkie ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c wisienkê koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171018-00000F2B7EEEDB27-F37490C5', 'Perfect Lady', 'Do wczeœniej wype³nionego lodem miksera wlaæ d¿in, bia³ka, sok cytrynowy oraz brandy. Miksowaæ przez oko³o 10 sekund. Delikatnie przecedziæ do kieliszka do szampana. Przybraæ atrakcyjnie wieszaj¹c plasterek brzoskwini.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171035-00000F2F59FE831D-304C3B5A', 'Pink Lady', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ d¿in, œmietanê, bia³ko, sok cytrynowy oraz grenadynê. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do kieliszka koktajlowego. Aby wygl¹da³o to efektowniej mo¿na obtaczaæ kieliszek w cukrze. Dekorowaæ zawieszaj¹c wisienkê koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171052-00000F3365AAA2D0-7CC150B6', 'Pink Pussycat', 'W shakerze umieœciæ 1/2 objêtoœci lód dodaæ d¿in, soki grenadyna. Mieszaæ przez ok. 5 sekund i przelaæ do szklanki typu Collins wczeœniej nape³nionego 3/4 lodem. Dekorujemy plasterkiem grejpfruta.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171107-00000F36E21AAC59-738211EC', 'Princeton', 'W szklanicy barmañskiej wczeœniej wype³nionej lodem umieœciæ d¿in, pomarañczówkê oraz ciemne piwo. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do szklanki typu oldfashioned. Aromatyzowaæ skórk¹ cytryny. Dekoracja skórka cytryny wrzucona do œrodka szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171127-00000F3B643A0280-3FA5A39E', 'Red Cloud', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ wszystkie sk³adniki. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do kieliszka koktajlowego. Przybraæ wisienk¹ koktajlow¹ oraz plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171150-00000F40DF983120-577D7AA1', 'Silver Bullet', 'Szklanicê barmañsk¹ wype³niæ w 1/2 lodem. Do tak przygotowanej dodaæ d¿in, brandy i dobrze wymieszaæ. Przelaæ do szk³a koktajlowego wczeœniej sch³odzonego. Na koniec aromatyzowaæ skórk¹ cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171206-00000F446D5C986E-CD5D3A49', 'Space', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ d¿in i sok z cytryny. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ szklanki typu oldfashioned. Dekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171223-00000F489BCF0059-62F26D67', 'Tangier', 'Shaker wype³niæ w 1/2 objêtoœci lodem. Wlaæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Delikatnie przecedziæ do szk³a koktajlowego. Dekoracjê wykonaæ z skórki pomarañcza, spiralka wrzucona do szk³a koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171301-00000F5161570AE9-72795699', 'Tod''s Cooler', 'W szklance typu highball wczeœniej wype³nionej lodem wlaæ d¿in, sok cytrynowy oraz creme de cassis. Nastêpnie zawartoœæ uzupe³niæ wod¹ gazowan¹. Dekoracja, skórka cytryny (spiralka).', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130171325-00000F56FE074B55-EDAA10F4', 'White Lady', 'W shakerze umieœciæ 1/2 objêtoœci lód dodaæ d¿in, sok i triple sec. Mieszaæ przez ok. 5 sekund i przelaæ do szk³a koktajlowego wczeœniej sch³odzonego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172054-00000FBF8F2C7BDA-2573AE62', 'Apres Ski', 'W shakerze wype³nionym w 1/2 objêtoœci lodem umieœciæ wszystkie p³ynne sk³adniki poza lemoniad¹. Mieszaæ energicznie przez 5 sekund. Przecedziæ do szklanki typu highball wczeœniej wype³nionej lodem. Dope³niæ lemoniad¹. Dekoracja ? plasterek cytryny zawieszony na brzegu szklanki z listkiem miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172127-00000FC71FAEBA63-835667FE', 'Balalaika', 'W shakerze wype³nionym w 1/2 objêtoœci lodem umieœciæ wszystkie p³ynne sk³adniki. Mieszaæ energicznie przez 5 sekund. Przecedziæ do szk³a koktajlowego. Dekorowaæ: plasterek pomarañczy oraz wisienka koktajlowa.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172151-00000FCCD5259EB0-46A78A42', 'Bailey''s Comet', 'W szklance typu oldfashioned wype³nionym w 1/2 objêtoœci lodem umieœciæ wódkê oraz Bailey''s. Zamieszaæ - gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172211-00000FD1578A2884-7174831A', 'Barbara', 'W shakerze wype³nionym lodem umieœciæ p³ynne sk³adniki. Mieszaæ przez 5 sekund. Przecedziæ do szk³a koktajlowego. Dekoracja: posypaæ na wierzch tart¹ czekolad¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172233-00000FD6A0D537AF-A20A8152', 'Black Cossack', 'Do du¿ej szklanki highball lub od piwa wlaæ wódkê, a nastêpnie piwo. Black Cossak jest gotowy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172316-00000FE098E0CF2D-E3C8FB24', 'Blenheim', 'W shakerze wype³nionym w 1/2 objêtoœci lodem umieœciæ wszystkie ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekoracjê wykonaæ z plasterka pomarañczy', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172332-00000FE456601EC3-E7D38CF4', 'Bloody Mary', 'W szklanice highball umieœciæ 2/3 objêtoœci kostki lodu. Do tak przygotowanej wlaæ wódkê oraz sok pomidorowy. Dodaæ przyprawy i zamieszaæ. Na koniec przybraæ ga³¹zk¹ selera kawa³kiem limonki oraz dorzuciæ mieszade³ko.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172416-00000FEE786843DE-7AD400C4', 'Borodino', 'W shakerze wype³nionym w 1/2 objêtoœci lodem umieœciæ wódkê, d¿in oraz Cointreau. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekoracjê wykonaæ z plasterka pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172432-00000FF2333C5C28-FA0BD2E6', 'Blue Shark', 'W shakerze wype³nionym w 1/2 objêtoœci lodem umieœciæ wódkê, d¿in oraz Curacao. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szk³a typu oldfashioned. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172449-00000FF63F2391F2-F7348277', 'Brazen Hussy', 'W shakerze wype³nionym w 1/2 objêtoœci lodem umieœciæ wódkê, sok cytrynowy oraz Cointreau. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172508-00000FFA7FBED770-2655822E', 'Bullfrog', 'Szklankê typu highball w 3/4 objêtoœci wype³niæ lodem. Do tak przygotowanej wlaæ wódkê, lemoniadê oraz Cointreau. Wymieszaæ pa³eczk¹ barmañsk¹. Dekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172527-00000FFF162DB455-5D62CD79', 'Camshaft', 'Szklankê typu highball w 3/4 objêtoœci wype³niæ t³uczonym lodem. Do tak przygotowanej wlaæ wszystkie ww. sk³adniki. Wymieszaæ pa³eczk¹ barmañsk¹. Dekorowaæ plasterkiem cytryny. Dekoracjê wykonaæ z plasterka cytryny oraz pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172545-000010032B714F2F-AE0992F1', 'Cape Coder', 'Szklankê typu highball w 3/4 objêtoœci wype³niæ t³uczonym lodem. Do tak przygotowanej wlaæ wszystkie ww. sk³adniki. Wymieszaæ pa³eczk¹ barmañsk¹. Dekoracjê wykonaæ z plasterka limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172601-00001007067201E1-F9CC1B33', 'Chilli Vodkatini', 'W szklanicy barmañskiej umieœciæ lód i wlaæ wódkê. Och³odziæ j¹ bardzo mocno mieszaj¹c. Kieliszek koktajlowy wymoczyæ w wermucie ? wlaæ, zakrêciæ kieliszkiem i wylaæ. Przecedziæ wódkê do kieliszka koktajlowego. Dekorowaæ kawa³kami chilli po³¹czonych z oliwk¹ i wrzuconych do œrodka kieliszka koktajlowego. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172618-0000100AD9E62381-7C1D353B', 'Cosmopolitan', 'W shakerze wype³nionym w 1/2 objêtoœci lodem umieœciæ wódkê, sok ¿urawinowy, sok z limy oraz Cointreau. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekoracja: spiralka ze skórki pomarañczy. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172637-0000100F61908D6F-D34549E5', 'Coochbehar', 'W shakerze wype³nionym w 1/2 objêtoœci lodem umieœciæ wódkê sok pomidorowy. Mieszaæ przez oko³o 5 sekund. Przelaæ do szklanki typu oldfashioned wype³nionej w 1/4 lodem. Dekoracja: str¹czek chilli.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172656-00001013D80F7E26-BC6604BE', 'Dunder Fizz', 'W shakerze wype³nionym w 1/2 objêtoœci lodem umieœciæ wódkê, soki oraz grenadynê. Mieszaæ przez oko³o 5 sekund. Przelaæ do szklanki typu highball wype³nionej w 1/2 lodem. Na koniec dope³niæ wod¹ gazowan¹. Dekoracja: plasterek cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172717-00001018C26E9CCC-678A1A47', 'Fire and Ice', 'W szklanicy barmañskiej umieœciæ du¿o lodu. Dodaæ wódkê oraz wermut. Dobrze wymieszaæ. Odcedziæ do szk³a koktajlowego. Nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172735-0000101CBF9F692D-BC7AE1D4', 'Firefly', 'Szklankê typu highball wype³niæ w 3/4 objêtoœci lodem. Jako pierwsz¹ wlaæ wódkê. Dodaæ sok oraz grenadynê. Dekorowaæ plasterkiem pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172756-00001021CF67E41A-189C65C7', 'French Horn', 'Szklanice barmañsk¹ nape³niæ lodem. Dodaæ wódkê oraz dwa pozosta³e sk³adniki. Dobrze wymieszaæ i przecedziæ do szk³a koktajlowego. Dekorowaæ malin¹, wrzuciæ do œrodka. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172817-0000102697AF4A09-E053B93F', 'Genoa', 'W shakerze umieœciæ lód i wlaæ wódkê Campari oraz sok. Dobrze wymieszaæ i przecedziæ do szklanki typu oldfashioned. Dekorowaæ spiralk¹ z skórki pomarañczy, wrzuciæ do œrodka. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172836-0000102AF87FE70A-7A704782', 'Gipsy', 'Wszystkie sk³adniki wlaæ bezpoœrednio do szklanki typu oldfashioned wczeœniej wype³nionej kruszonym lodem i dobrze ze sob¹ wymieszaæ. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172853-0000102F120081BF-437E1053', 'Golden Russian', 'Wszystkie sk³adniki wlaæ bezpoœrednio do szklanki typu oldfashioned wczeœniej wype³nionej kruszonym lodem i dobrze ze sob¹ wymieszaæ. Dekorowaæ plasterkiem limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172910-00001032E97A7D79-DFDD660A', 'Graffiti', 'Wszystkie sk³adniki wlaæ bezpoœrednio do kieliszka od wina wczeœniej wype³nionego kruszonym lodem i wymieszaæ ze sob¹. Dekorowaæ jednym winogronem oraz wisienk¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172926-00001036A99A9FD6-CE9FDD57', 'Greendragon', 'Wódkê oraz likier umieœciæ w shakerze wype³nionym lodem. Mieszaæ przez 5 sekund. Nastêpnie przecedziæ do kieliszka koktajlowego. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130172943-0000103AA33254C8-5B1DFCB0', 'Hammer Horror', 'Mikser wype³niæ kruszonym lodem i wlaæ tam wódkê, Kahlue oraz lody waniliowe. Miksowaæ przez 5 sekund. Nastêpnie przecedziæ do kieliszka koktajlowego. Dekorowaæ posypuj¹c na wierzch tart¹ czekolad¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173000-0000103E9CF1F460-42C63B0F', 'Harvey', 'Na pocz¹tku szklankê typu highball wype³niæ w 1/2 objêtoœci lodem. Wlaæ tam wódkê oraz sok pomarañczowy i spokojnie wymieszaæ. Dodaæ ostro¿nie Galliano. Dekorowaæ plasterkiem pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173015-0000104231A6377B-92C8AB2F', 'Hawaiian Vodka', 'Shaker wype³niæ lodem i wlaæ wszystkie ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Odcedziæ do szklanki typu oldfashioned wype³nionej w 1/2 objêtoœci lodem. Dekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173032-00001045FB1411AA-32BA9084', 'Hot Cherry', 'Shaker wype³niæ lodem i wlaæ wódki. Mieszaæ przez oko³o 5 sekund. Odcedziæ do szklanki typu oldfashioned. Uzupe³niæ tonikiem. Dekorowaæ wisienk¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173051-0000104A91927EC9-07804088', 'Kamikaze', 'Shaker wype³niæ lodem i wlaæ wódkê oraz sok z limy. Mieszaæ przez oko³o 5 sekund. Odcedziæ do kieliszka koktajlowego. Na jeden oddech.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173112-0000104F4C34FE76-BCF2EE5F', 'Kangaroo', 'Shaker wype³niæ lodem i wlaæ sk³adniki. Mieszaæ przez oko³o 5 sekund. Odcedziæ do kieliszka koktajlowego. Dekorowaæ wrzucaj¹c oliwkê na dno kieliszka. Aby poprawiæ zapach, aromatyzowaæ skórk¹ cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173132-00001054007B6FF2-9DE3F822', 'Katinka', 'Shaker wype³niæ lodem i wlaæ sk³adniki. Mieszaæ przez oko³o 5 sekund. Odcedziæ do kieliszka koktajlowego. Dekorowaæ plasterkiem limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173201-0000105AA92CE4DC-86FE618D', 'Kempinsky Fizz', 'W szklance typu highball umieœciæ w 3/4 objêtoœci lód. Wlaæ wódkê, sok z cytryny oraz creme de chassis. Sk³adniki wymieszaæ ze sob¹, a nastêpnie zawartoœæ dope³niæ piwem. Dekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173217-0000105E902AC24D-A0C74324', 'Kir Lethale', 'W kieliszku do szampana umieœciæ rodzynka. Wlaæ wódkê oraz creme de cassis. Zawartoœæ dope³niæ szampanem. Nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173236-00001062F48FA7C9-8556E99C', 'Laughing at the waves', 'W szkalnicy barmañskiej umieœciæ lód, a nastêpnie wlaæ tam wszystkie sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szk³a koktajlowego. Skórkê cytryny wykorzystaæ do aromatyzowania. Nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173256-000010677E55698F-28C6DAEF', 'Liberator', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ tam wódkê, likier oraz soki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekoracjê wykonaæ z plasterka limy oraz kawa³ka mango.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173312-0000106B63656B01-B94E05FB', 'Long Island ice tea', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki poza col¹. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szklanki typu Collins w 1 objêtoœci wype³nionej lodem. Zawartoœæ uzupe³niæ col¹. Dekoracjê wykonaæ z plasterka cytryny. Dodatkowy plasterek nale¿y wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173330-0000106F81425A68-A1879C85', 'Madras', 'W szklance typu highball umieœciæ 3/4 objêtoœci lodu. Do tak przygotowanej wlaæ wszystkie sk³adniki. Dekorowaæ plasterkiem pomarañczy, jeden te¿ nale¿y wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173350-000010741D78B48E-3C1A7B46', 'Mint Collins', 'W szklance typu highball umieœciæ 3/4 objêtoœci lodu. Do tak przygotowanej wlaæ wszystkie sk³adniki poza wod¹. Zamieszaæ, a nastêpnie uzupe³niæ wod¹ gazowan¹. Dekorowaæ plasterkiem cytryny, wrzuciæ do œrodka ga³¹zkê miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173408-000010786A6EA39E-776BDF57', 'Moscow Mule', 'W szklance typu oldfashioned umieœciæ 1/2 objêtoœci lodu. Do tak przygotowanej wlaæ wódkê oraz sok. Zamieszaæ, a nastêpnie uzupe³niæ piwem. Dekorowaæ plasterkiem limy, kawa³ki limy wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173428-0000107CF0AEAFF5-69F0C942', 'Nevesky Prospekt', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekoracjê wykonaæ z plasterka limy. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173448-000010818A773DD4-DEFE17CA', 'Piranha', 'W szklance typu oldfashioned umieœciæ 1/2 objêtoœci lodu. Do tak przygotowanej wlaæ wódkê oraz creme de cacao. Zamieszaæ, a nastêpnie uzupe³niæ col¹. Nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173505-000010859981AF9F-7A6E1879', 'Pompanski', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szklanki typu oldfashioned wype³nionej lodem. Dekoracjê wykonaæ ze skórki grejpfruta. Mo¿na te¿ wrzuciæ plasterek grejpfruta do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173523-00001089DFD3B722-286528CB', 'Prussion Salute', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekoracjê wykonaæ plasterka cytryny. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173538-0000108D4F1C9BF8-3EB53305', 'Purple Passion', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szklanki typu highball w 3/4 objêtoœci wype³nionej lodem. Dekoracjê wykonaæ plasterka cytryny. Inni plasterek wrzuciæ do œrodka. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173713-000010A36482536A-99D59C9A', 'Salty Dog', 'Shaker wype³niæ lodem, a nastêpnie wlaæ wódkê oraz sok grejpfrutowy. Mieszaæ przez oko³o 5 sekund. Przygotowaæ szklankê typu highball: wysypaæ sól na p³askiej powierzchni. Krawêdzie szklanki przeci¹gn¹æ limon¹, a nastêpnie zanurzyæ w warstwie soli. Zawartoœæ z shakera przecedziæ do tak przygotowanje szklanki w 1/2 objêtoœci wype³nionej lodem. Dekoracjê wykonaæ plasterka limy. Inni plasterek wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173729-000010A71F98C9AF-7C89B069', 'Screwdriver', 'Szklankê typu oldfashioned wype³niæ w 1/2 objêtoœci lodem. Wlaæ wódkê oraz sok pomarañczowy. Zamieszaæ przez chwilê. Dekorowaæ wrzucaj¹c plasterek pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173743-000010AA51520AD9-0F08B95F', 'Sea Breeze', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szklanki typu highball w 3/4 objêtoœci wype³nionej lodem. Dekoracjê wykonaæ plasterka limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173757-000010ADB691490B-43B13458', 'Sex on the Beach', 'Szklankê typu highball wype³niæ w 1/2 objêtoœci lodem. Wlaæ wódkê pozosta³e sk³adniki. Zamieszaæ energicznie przez chwilê. Dekorowaæ wrzucaj¹c plasterek pomarañczy i zawieszaj¹c plasterek ananasa.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173813-000010B1549E9662-E170AAA1', 'Sos', 'Jak wykonaæ: Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego w 3/4 objêtoœci wype³nionego lodem. Dekoracjê wykonaæ z plasterka cytryny. W³o¿yæ krótk¹ s³omkê i gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173830-000010B557BB814D-200E7DAB', 'Soviet', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Aby nadaæ jeszcze lepszy aromat wycisn¹æ nad powierzchni¹ kieliszka skórkê cytryny i wrzuciæ j¹ do œrodka. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173846-000010B90C22BA55-33B83F1D', 'Vodkatini', 'Do kieliszka koktajlowego wlaæ wódkê oraz wytrawny wermut wczeœniej och³odzony w szklanicy barmañskiej wype³nionej lodem. Delikatnie nad powierzchni¹ kieliszka wycisn¹æ skórkê cytryny. Dekorowaæ wrzucaj¹c spiralkê z skórki cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173905-000010BD78A5D765-0C7E9C63', 'Volga', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ szklanki typu oldfasioned wype³nionej wczeœniej lodem w 1/2 objêtoœci. Dekorowaæ zawieszaj¹c na krawêdzi szklanki plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130173920-000010C10313E9A5-27A71F23', 'Woo Woo', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wódkê, sok oraz brzoskwiniowy napój. Mieszaæ przez oko³o 5 sekund. Przecedziæ szklanki typu highball wype³nionej wczeœniej lodem w 1/2 objêtoœci. Dekorowaæ zawieszaj¹c na krawêdzi szklanki plasterek brzoskwini.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174142-000010E217BDDBF3-B852AFB8', 'Ambasador', 'Szklanicê barmañska wype³niæ w 1/2 objêtoœci lodem i wlaæ tequilê, sok oraz syrop cukrowy. Energicznie mieszaæ przez 5 sekund. Nastêpnie zawartoœæ przelaæ do szklanki typu oldfashioned wczeœniej wype³nionej lodem. Dekorowaæ zawieszaj¹c na krawêdzi szklanki plasterek pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174159-000010E5F7289E51-54DEF209', 'Bar Bandit', 'Do szklanki typu highball wype³nionej w 1/2 objêtoœci lodem t³uczonym wlaæ wszystkie wy¿ej wymienione sk³adniki pomijaj¹c napój gazowany. Energicznie wymieszaæ ze sob¹ oraz zawartoœæ wedle uznania uzupe³niæ wiœniowym napojem gazowanym. Dekorowaæ wrzucaj¹c wisienkê koktajlow¹ oraz zawieszaj¹c na krawêdzi szklanki plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174220-000010EADCC83554-4922CFCB', 'Boomer', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ tequilê, sok, brandy oraz syrop cukrowy. Mieszaæ przez oko³o 5 sekund. Przecedziæ do bardzo ma³ego szk³a koktajlowego. Gotowe, na jeden oddech...', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174238-000010EF2473F2D6-D5E3893B', 'Cactus Juice', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szklanki typu oldfashioned wczeœniej wype³nionej lodem. Dekorowaæ plasterkiem cytryny zawieszaj¹c go na krawêdzi szklanki. Mo¿na siê pokusiæ o wrzucenie jednego plasterka do œrodka szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174254-000010F2C5DC113E-6164C25D', 'Cherrycoco', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie sk³adniki. Mieszaæ przez oko³o 5 sekund. Przelaæ do szklanki typu highball wczeœniej wype³nionej lodem. Dekorowaæ zawieszaj¹c na krawêdzi szklanki wisienkê koktajlow¹. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174309-000010F669208687-A06986B6', 'Chimayo Cocktail', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie ww. sk³adniki. Mieszaæ energicznie przez oko³o 5 sekund. Przelaæ do szklanki typu oldfashioned wczeœniej wype³nionej lodem. Dekorowaæ zawieszaj¹c na krawêdzi szklanki z¹bek jab³ka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174326-000010FA30A24AAF-CEC61225', 'Border Crossing', 'Do szklanki typu highabll wype³nionej w 3/4 objêtoœci lodem wlaæ wszystkie wy¿ej wymienione sk³adniki pomijaj¹c colê. Energicznie wymieszaæ ze sob¹ oraz zawartoœæ wedle uznania uzupe³niæ col¹. Dekorowaæ wrzucaj¹c do œrodka oraz zawieszaj¹c na krawêdzi szklanki plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174341-000010FDB752AE0A-6ED2A739', 'California Dream', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie ww. sk³adniki. Mieszaæ delikatnie przez oko³o 5 sekund. Przelaæ do szk³a koktajlowego. Dekorowaæ wrzucaj¹c do kieliszka wisienkê koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174355-000011011193324E-17F22126', 'Doralto', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie ww. sk³adniki pomijaj¹c tonic. Mieszaæ przez 5 sekund, a nastêpnie przelaæ do szklanki typu highball w 3/4 objêtoœci wype³nionej lodem. Dekorowaæ zawieszaj¹c ma krawêdzi szklanki plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174411-00001104BF51506F-5988F347', 'El dorado', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie ww. sk³adniki. Mieszaæ bardzo energicznie przez 5 sekund, a nastêpnie przelaæ do szklanki typu highball w 3/4 objêtoœci wype³nionej lodem. Dekorowaæ zawieszaj¹c ma krawêdzi szklanki plasterek pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174427-000011086135A8A2-25370D04', 'Firebird', 'Szklankê typu oldfashioned wype³niæ prawie w ca³oœci t³uczonym lodem. Wlaæ tequilê, sok oraz creme de banana. Zamieszaæ i uzupe³niæ lemoniad¹. Dekorowaæ zawieszaj¹c ma krawêdzi szklanki plasterek cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174444-0000110C7307B8DB-2BC4837D', 'Icebreaker', 'Mikser wype³niæ lodem kruszonym (bez przesady) i wlaæ tam przedstawione wy¿ej sk³adniki. Miksowaæ przez oko³o 10 sekund. Nastêpnie zawartoœæ przecedziæ do kieliszka do wina wczeœniej bardzo mocno sch³odzonego ? powinien mieæ szron na œciankach. Dekorowaæ plasterkiem grejpfruta.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174500-00001110110DE7C6-36047FC6', 'Last Chance', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ tequilê, brandy, miód oraz sok z limy. Mieszaæ bardzo energicznie przez 5 ? 10 sekund a¿ siê miód rozpuœci, a nastêpnie przelaæ do szklanki typu oldfashioned w 1/2 objêtoœci wype³nionej lodem. Dekorowaæ zawieszaj¹c ma krawêdzi szklanki plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174514-000011137EF3AAE8-C7F19268', 'Margarita', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ tequilê, Cointreau oraz sok z limy. Mieszaæ przez 5 sekund. Krawêdzie kieliszka koktajlowego przeci¹gn¹æ lim¹, a nastêpnie zamoczyæ w soli. Do tak przygotowanego przelaæ zawartoœæ z shakera. Dekorowaæ zawieszaj¹c ma krawêdzi spiralkê z skórki limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174532-00001117A4CEA3E4-AC8F7C90', 'Massacre', 'Szklankê typu highball wype³niæ w 3/4 objêtoœci lodem. Na wierzch wlaæ sk³adniki wymienione powy¿ej. Zamieszaæ i udekorowaæ s³omk¹. Gotowe', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174549-0000111BA2ED0634-414142A9', 'Mexico', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ tequilê, soki oraz grenadynê. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do kieliszka koktajlowego. Dekorowaæ po³ówk¹ plasterka cytryny zawieszaj¹c j¹ na krawêdzi kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174605-0000111F31EBEF68-DBC3B8D0', 'Mexican Itch', 'Wysypaæ na rêkê sól. Wzi¹æ do rêki kawa³ek limy oraz kieliszek z tequil¹. Jak siê z tym uporaæ: raz: polizaæ sól, dwa: tequila, trzy: zagryŸæ lim¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174634-0000112600644085-F69F16EB', 'Mocking Bird', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ tequilê, sok i creme de menthe. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do sch³odzonego kieliszka koktajlowego. Dekorowaæ po³ówk¹ plasterka limy oraz ³ody¿k¹ miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174649-000011299991C5AC-91D64E42', 'Pepper-Eater', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie wy¿ej wymienione sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do szklanki typu oldfashioned w 3/4 objêtoœci wype³nionej lodem (najlepiej dodatkowo wczeœniej sch³odzonej). Dekorowaæ papryk¹ chilli, wrzuciæ do œrodka szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174708-0000112E0774C941-17A14017', 'Poker Face', 'Szklankê typu highball wype³niæ w 3/4 objêtoœci lodem. Na wierzch wlaæ tequilê, sok ananasowy oraz Cointreau. Wymieszaæ i dekorowaæ plasterkiem limy za³o¿onym na krawêdŸ szklanki. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174742-00001135FA2839BD-2B5529C7', 'Ridley', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie wy¿ej wymienione sk³adniki poza Galliano. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do kieliszka do szampana w 3/4 objêtoœci wype³nionej kruszonym lodem. Na koniec wlaæ Galliano. Dekorowaæ zawieszaj¹c plasterek pomarañczy na krawêdzi kieliszka oraz wrzuciæ do œrodka wisienkê koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174754-0000113899849D50-5441AFE1', 'Rosita', 'Jak wykonaæ: Szklankê typu highball wype³niæ w 3/4 objêtoœci lodem. Na wierzch wlaæ tequilê i pozosta³e sk³adniki. Dobrze wymieszaæ. Aby poprawiæ aromat wycisn¹æ nad powierzchni¹ szklanki skórkê cytryny i wrzuciæ j¹ do œrodka. Dekorowaæ plasterkiem cytryny za³o¿onym na krawêdŸ szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174813-0000113D19FCE460-5C2BA24F', 'Spanish Fly', 'Szklankê typu oldfashioned wype³niæ w 3/4 objêtoœci t³uczonym lodem (a nawet wiêcej). Wlaæ tequilê i sherry. Zamieszaæ. Serwowaæ z bardzo tward¹ i cieniutk¹ s³omk¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174832-000011418C3B7EC4-CB93CEB1', 'Submarino', 'Do szklanki od piwa wczeœniej bardzo mocno sch³odzonej wlaæ piwo i dodaæ tequili. Pamiêtaj aby sk³adniki, których u¿ywasz by³y odpowiednio mocno sch³odzone.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174854-00001146A6430020-344D68EE', 'Tequila Orange', 'Szklankê typu highball wype³niæ w 1/2 objêtoœci lodem. Wlaæ tequilê i sok pomarañczowy. Zamieszaæ. Dekorowaæ: jeden plasterek pomarañczy wrzuciæ do œrodka szklani, a drugi zawiesiæ na krawêdzi. Serwowaæ z bardzo tward¹, cieniutk¹ ale d³ug¹ s³omk¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174911-0000114AB207706D-909141EB', 'Tequila Slammer', 'Do kieliszka od wódki wlaæ tequilê oraz lemoniadê lub na wymianê wino musuj¹ce. Kieliszek (wczeœniej d³ugo ch³odzony) przykryæ rêk¹ i w momencie kiedy chcemy go wypiæ dwa razy energicznie uderzyæ w stó³. Uwaga ? trzeba go niezw³ocznie przechyliæ i wpiæ, gdy¿ bardzo szybko wyp³ynie z kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174927-0000114E467D8873-54DBED9F', 'Tequila Sunrise', 'Do szklanki typu highball wype³nionej w 3/4 objêtoœci lodem wlaæ tequilê oraz sok pomarañczowy. Zamieszaæ. Teraz energicznie tak aby sp³ynê³a na dó³ szklanki wlaæ grenadynê. Delikatnie aby nie zm¹ciæ napoju wrzuciæ plasterek pomarañczy, a na krawêdzi zawiesiæ plasterek pomarañczy', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130174946-00001152B6BAEFC2-31D34ABB', 'Tequila Moonrise', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie wy¿ej wymienione sk³adniki poza piwem. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do szklanki typu Collins w 3/4 objêtoœci wype³nionej lodem. Na koniec wlaæ piwo. Dekorowaæ zawieszaj¹c plasterek pomarañczy na krawêdzi kieliszka oraz wrzuciæ jeden do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130175002-00001156589C2C6C-CDFA2110', 'Tequila Sunset', 'Do kieliszka koktajlowego wczeœniej bardzo mocno sch³odzonego wlaæ tequilê oraz soki. Zamieszaæ kilkakrotnie. Teraz dodaæ miód tak zgrabnie aby opad³ na dó³ kieliszka. Na sam koniec creme de chassis. Gotowe. Nie wymaga dekorowania.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130175028-0000115C99D84A59-C5A5A885', 'Viva Villa', 'Shaker wype³niæ lodem, a nastêpnie wlaæ tequilê i soki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do? STOP - Nale¿y przygotowaæ odpowiednio kieliszek koktajlowy. Przeci¹gn¹æ jego brzegi lim¹ i zamoczyæ we wczeœniej rozsypanym cukrze. Teraz mo¿na ju¿ spokojnie przecedziæ zawartoœæ shakera do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c plasterek limy na krawêdzi kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130175044-0000116040DB423B-43FB04A7', 'Zorro', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie wy¿ej wymienione sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c plasterek limy na krawêdzi kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183124-000013984C1AC99E-9CC79721', 'Affinity', 'Szklanicê barmañsk¹ wype³niæ lodem, a nastêpnie wlaæ tam whisky, wermuty oraz wódkê. Wszystko ze sob¹ dobrze wymieszaæ i przecedziæ do szk³a koktajlowego. Na koniec kiedy szklanka jest ju¿ pe³na wycisn¹æ nad jej powierzchni¹ skórkê cytryny. Dekorowaæ zawieszaj¹c po³ówk¹ plasterka cytryny na krawêdzi szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183140-0000139C007CC692-25E803EF', 'Artist''s Special', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie wy¿ej wymienione sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do kieliszka koktajlowego. Nie wymaga dekorowania.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183150-0000139E60B28631-F6DECF67', 'Algonquin', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ whisky, sok oraz wermut. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do szklanki typu oldfashioned w 3/4 objêtoœci wype³nionej t³uczonym lodem. Podawaæ ze s³omk¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183159-000013A07A80938D-B2C32421', 'Blizzardl', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie wy¿ej wymienione sk³adniki (poza cytryn¹ i wisienk¹ koktajlow¹). Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do szklanki typu highball w 1/2 objêtoœci wype³nionej lodem. Dekoracja: zawiesiæ plasterek cytryny na krawêdzi szklanki oraz wrzuciæ wisienkê koktajlow¹ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183213-000013A3D0FF01A3-7FB006D5', 'Booby Burns', 'Szklanicê barmañsk¹ wype³niæ lodem, a nastêpnie wlaæ tam whisky, wermut oraz Benedictine. Wszystko ze sob¹ wymieszaæ i przecedziæ do szk³a koktajlowego wczeœniej dobrze sch³odzonego Na koniec kiedy szklanka jest ju¿ pe³na wycisn¹æ nad jej powierzchni¹ skórkê cytryny. Dekorowaæ wrzucaj¹c po³ówk¹ plasterka cytryny do szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183231-000013A7E92E5A57-9355FD92', 'Brainstorm', 'Do szklanki typu oldfashioned wczeœniej bardzo mocno sch³odzonego i nape³nionej w 2/3 objêtoœci lodem wlaæ wymienione sk³adniki. Zamieszaæ.. Na koniec dekorowaæ plasterkiem pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183243-000013AAA227E6DD-F2B1658E', 'Buckaroo', 'Do szklanki typu highball wczeœniej nape³nionej w 2/3 objêtoœci lodem wlaæ bourbon oraz angosturê. Zamieszaæ. Zawartoœæ uzupe³niæ wedle uznania col¹ Na koniec wrzuciæ mieszade³ko do œrodka szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183254-000013AD64576531-B80A1C2C', 'Cablegram', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie wy¿ej wymienione sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do szklanki typu highabll w 2/3 objêtoœci wype³nionej lodem. Dekoracja: zawiesiæ spiralkê ze skórki cytryny na krawêdzi szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183305-000013AFC4CC2D6C-DFB58243', 'Canada Cocktail', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie wy¿ej wymienione sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ kieliszka koktajlowego. Dekoracja: zawiesiæ spiralkê ze skórki pomarañczy na krawêdzi szklanki tak aby wpada³a do kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183313-000013B1D0D6C0D9-C2AFD34D', 'Duck Soap', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ bourbon, soki oraz brandy. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do szklanki typu oldfashioned wype³nionej w 3/4 objêtoœci lodem. Dekoracja: zawiesiæ plasterek cytryny na krawêdzi szklanki oraz do œrodka wrzuciæ wisienkê koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183323-000013B40C9792B4-41E26298', 'Fancy Bourbon', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ bourbon i pozosta³e sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do szklanki typu oldfashioned wype³nionej w 3/4 objêtoœci t³uczonym lodem. Dekoracja: spiralkê ze skórki cytryny wrzuciæ do œrodka szklanki zahaczaj¹c j¹ o jej krawêdzie.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183333-000013B65C103ED1-710C238B', 'Gall Bracer', 'W szklanicy barmañskiej umieœciæ kostki lodu. Dodaæ whisky, grenadynê i angosturê. Wszystkie sk³adniki dobrze ze sob¹ wymieszaæ i przelaæ do szklanki typu old fashioned wczeœniej wype³nionej trzema kostkami lodu. Nad powierzchni¹ wycisn¹æ delikatnie skórkê cytryny. Na koniec przybraæ wisienk¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183343-000013B8BB481B7E-BCACD99C', 'Grenoble', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ bourbon i pozosta³e sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do kieliszka koktajlowego. Dekoracja: plasterek pomarañczy oraz wrzucona malina (mo¿e byæ nabita na szpadkê).', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183352-000013BABDC91835-4B1D9F89', 'Highball', 'Do szklanki typu highball wczeœniej nape³nionej w 1/3 objêtoœci lodem wlaæ bourbon oraz wodê. Zamieszaæ. Na koniec aromatyzowaæ wyciskaj¹c nad powierzchni¹ skórkê cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183401-000013BCE1AD442C-A1264726', 'Irish Chocolate', 'Po³owê zawartoœci œmietany ubiæ na sztywn¹ pianê. W tym czasie podgrzaæ mleko i czekoladê. Dodaæ kakao. Mieszaæ i zagotowaæ. Nastêpnie dodaæ whisky i pozosta³¹ czêœæ œmietany. Zamieszaæ, zagotowaæ i przelaæ do dwóch szklanek (musz¹ byæ ¿aroodporne gdy¿ napój jest bardzo ciep³y). Dekorowaæ wedle uznania i smaku: wiórki startej czekolady oraz bit¹ œmietan¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183411-000013BF56A5938C-0E734806', 'Jack Frost', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie wy¿ej wymienione sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do kieliszka koktajlowego w 1/2 objêtoœci wype³nionego lodem. Dekorowaæ: plasterek cytryny oraz kawa³ek ananasa (mo¿e byæ nabity na szpadkê).', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183420-000013C14513D87E-252594D5', 'Jamaica Shake', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie wy¿ej wymienione sk³adniki poza czekolad¹. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do kieliszka koktajlowego. Dekorowaæ: posypaæ na wierzch tart¹ czekolad¹ wedle uznania.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183428-000013C321EA553C-7B7F200C', 'Kentucky Kernel', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ bourbon, brandy, grenadynê oraz sok. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przelaæ do szklanki typu old fashioned. Ewentualnie dekorowaæ plasterkiem grejpfruta.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183436-000013C51B6BCB4C-18C57B5B', 'Loch Lomond', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ whisky, angosturê oraz syrop cukrowy. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do kieliszka koktajlowego. Na koniec wycisn¹æ kilka kropel cytryny do kieliszka i wrzuciæ j¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183446-000013C75059DDA2-346DF560', 'Loch Ness', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ wszystkie sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do szklanki typu old fashioned. Nie wymaga dekoracji.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183454-000013C958197FA8-C0021B78', 'Manhatan', 'Shaker wype³niæ w 3/4 objêtoœci lodem, a nastêpnie umieœciæ wszystkie sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do szklanki typu old fashioned. Dekorowaæ wrzucaj¹c wisienkê koktajlow¹ do szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183503-000013CB4CDAD320-B584ED70', 'Millionaire', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ wszystkie sk³adniki. Mieszaæ przez 5 sekund. Tak wymieszan¹ zawartoœæ shakera przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c plasterek cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183511-000013CD46AF3D4E-82DD7AED', 'Mint Julep', 'W szklanicy barmañskiej umieœciæ cukier i miêtê. Pocieraj¹c t³uczkiem rozdrobniæ miêtê, a¿ puœci soki. Dodaæ gor¹c¹ wodê. Wszystkie sk³adniki jeszcze raz dobrze wymieszaæ i przelaæ do szklanki typu highball wczeœniej wype³nionej kruszonym lodem. Wlaæ bourbon. Mieszaæ do momentu pojawienia siê szronu na szklance.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183523-000013CFF9B9AC20-FED84B10', 'New York', 'W szklanicy barmañskiej wype³nionej 1/3 lodem umieœciæ wszystkie ww. sk³adniki. Mieszaæ przez ok. 5 sekund i przelaæ do kieliszka koktajlowego. Na koniec aromatyzowaæ skórk¹ pomarañczy wyciskaj¹c bezpoœredni nad szklank¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183532-000013D22F2EDC72-FB4F1069', 'Old fashioned', 'Bezpoœrednio do szklanki typu old fashioned wlaæ angosturê oraz syrop cukrowy. Dobrze wymieszaæ. Dodaæ whisky i wype³niæ szklankê kruszonym lodem w 3/4 objêtoœci. Jeszcze raz zamieszaæ - tym razem delikatnie. Dekorowaæ wrzucaj¹c do œrodka wisienkê koktajlow¹ oraz plasterek pomarañczy. Zawiesiæ na krawêdzi spiralkê z skórki cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183541-000013D44350E7F8-3C3A1581', 'Paddy', 'Shaker wype³niæ lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekoracjê wykonaæ z plasterka pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183550-000013D658743A01-E94D4375', 'Pamplemousse', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szklanki typu old fashioned w 3/4 nape³nionej t³uczonym lodem. Dekorowaæ: skórkê pomarañczy nabiæ na wyka³aczkê razem z wisienk¹ koktajlow¹. Zawiesiæ tak¹ dekoracjê na krawêdzi szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183559-000013D84CA49B05-06012F91', 'Perfect Manhattan', 'W szklanicy barmañskiej wype³nionej 1/3 lodem umieœciæ whisky oraz wermuty. Mieszaæ przez ok. 20 sekund i przecedziæ do wczeœniej sch³odzonego kieliszka koktajlowego. Aromatyzowaæ skórk¹ cytryny wyciskaj¹c bezpoœredni nad i wrzuciæ j¹ do kieliszka. Na koniec wrzuciæ wisienkê koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183608-000013DA7F469162-85468974', 'Plank Walker', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ whisky, likier oraz wermut. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szklanki typu old fashioned w 3/4 nape³nionej t³uczonym lodem. Dekorowaæ: wrzuciæ kawa³ek œwie¿ej cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183616-000013DC5583499D-DFEB77F9', 'Pleasant dreams', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do wczeœniej sch³odzonej szklanki typu old fashioned w 3/4 nape³nionej lodem. Dekorowaæ: zawiesiæ na krawêdzi kawa³ek œwie¿ej brzoskwini.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183624-000013DE2666FDF1-3101DCD5', 'Ring of Kerry', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekorowaæ posypuj¹c tart¹ czekolad¹ wierzch kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183637-000013E11C0AF430-0FBE76B8', 'Rob Roy', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie wlaæ whisky, wermut oraz angosturê. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183645-000013E314A1F20A-DE2B4588', 'Rocky Mountain', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie wlaæ whisky oraz likier. Mieszaæ przez oko³o 5 sekund. Przelaæ do szklanki typu oldfashioned w 2/3 wype³nionej lodem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183654-000013E51CD6870E-B5968D78', 'Rusty Nail', 'Bezpoœrednio do szklanki typu oldfashioned wype³nionej w 2/3 lodem wlaæ whisky oraz likier. Delikatnie zamieszaæ. Nie wymaga dekorowania.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183702-000013E7199A2649-B8A0EA95', 'Sea Dog', 'W szklance typu highball umieœciæ 2 kostki cukru, a nastêpnie zalaæ angostur¹. PóŸniej dodaæ z¹bki pomarañczy oraz cytryny. To wszystko tak przygotowanie zasypaæ t³uczonym lodem. Na sam koniec dodaæ whisy oraz Benedictine, a zawartoœæ wedle uznania uzupe³niæ wod¹ gazowan¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183711-000013E9280ADE11-996E046D', 'Suburban', 'W szklanicy barmañskiej wype³nionej 1/3 lodem umieœciæ whisky, rum, porto oraz wódkê gorzk¹. Mieszaæ przez ok. 5 sekund i przecedziæ do wczeœniej sch³odzonej wype³nionej w 2/3 objêtoœci lodem szklanki typu oldfashioned. Na koniec mo¿na dekorowaæ zawieszaj¹c spiralkê z cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183719-000013EB01000259-B95EA348', 'Sour', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 3 sekund. Przecedziæ do szklanki typu oldfashioned. Dekorowaæ zawieszaj¹c spiralkê z cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183727-000013ECC706827E-7AE17D15', 'Strong Arm', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183735-000013EEB3FDCCC7-29349E4F', 'Tipperary', 'W szklanicy barmañskiej wype³nionej 1/3 lodem umieœciæ whisky i rum. Mieszaæ przez ok. 5 sekund i przecedziæ do wczeœniej sch³odzonego kieliszka koktajlowego. Dekorowaæ zawieszaj¹c kawa³eczek pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183742-000013F0725DEC37-F5612CF1', 'Tivoli', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekorowaæ plasterkiem pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183750-000013F24A79120B-72C98D2D', 'The Bairn', 'W szklance typu oldfashioned umieœciæ du¿o t³uczonego lodu, a nastêpnie wlaæ sk³adniki. Zamieszaæ delikatnie aby wymiesza³y siê ze sob¹ sk³adniki. Na koniec udekorowaæ zawieszaj¹c plasterek pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183758-000013F42468DBE8-5CCA9501', 'Thunderclap', 'W szklanicy barmañskiej wype³nionej 1/3 lodem umieœciæ whisky, brandy oraz d¿in. Mieszaæ przez ok. 5 sekund i przecedziæ do wczeœniej sch³odzonego kieliszka koktajlowego. Nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183806-000013F60CAD1229-7BE0F688', 'Tiger Juice', 'W szklanicy barmañskiej wype³nionej 2/3 lodem umieœciæ whisky oraz soki. Mieszaæ przez ok. 5 sekund i przecedziæ do wczeœniej sch³odzonego kieliszka koktajlowego. Ewentualnie dekorowaæ spiralk¹ z skórki pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183814-000013F7E1713225-79D75125', 'Ward Eight', 'W szklanicy barmañskiej wype³nionej 2/3 t³uczonym lodem umieœciæ whisky, soki oraz grenadynê. Mieszaæ przez ok. 5 sekund i przecedziæ do wczeœniej sch³odzonej wype³nionej w 1/3 objêtoœci t³uczonym lodem szklanki typu highball. Uzupe³niæ wg uznania wod¹ gazowan¹ Dekorowaæ zawieszaj¹c plasterek pomarañczy i cytryny. Jeden mo¿na wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183823-000013F9D5A3D35A-8BE6155F', 'Whisky Mac', 'Bezpoœrednio do szklanki typu oldfashioned wype³nionej kilka kostkami lodu wlaæ whisky oraz wino. Zamieszaæ i niczym nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183832-000013FC02104A0F-D1FF2737', 'Whisky Sour', 'W szklanicy barmañskiej rozgnieœæ po³ówkê brzoskwini . Shaker wype³niæ w 2/3 objêtoœci kruszonym lodem. Umieœciæ rozgniecion¹ brzoskwiniê oraz wszystkie ww. sk³adniki poza wod¹ gazowan¹. Mieszaæ przez ok. 5 sekund i przecedziæ do wczeœniej sch³odzonej zawieraj¹cej kilka kostek lodu szklanki typu highball. Uzupe³niæ wg uznania wod¹ gazowan¹. Dekorowaæ zawieszaj¹c plasterek pomarañczy. Podawaæ ze s³omk¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130183841-000013FE2E03ECB7-3F27425C', 'Whisky Guirt', 'W szklanicy barmañskiej rozgnieœæ po³ówkê brzoskwini . Shaker wype³niæ w 2/3 objêtoœci kruszonym lodem. Umieœciæ rozgniecion¹ brzoskwiniê oraz wszystkie ww. sk³adniki poza wod¹ gazowan¹. Mieszaæ przez ok. 5 sekund i przecedziæ do wczeœniej sch³odzonej zawieraj¹cej kilka kostek lodu szklanki typu highball. Uzupe³niæ wg uznania wod¹ gazowan¹. Dekorowaæ zawieszaj¹c plasterek pomarañczy. Podawaæ ze s³omk¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184100-0000141E7815EC30-0B9ABF02', 'Alhambra', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, Malibu oraz soki. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do wype³nionej lodem w 2/3 objêtoœci szklanki typu highball. Dekorowaæ zawieszaj¹c na krawêdzi plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184111-00001420F59FF549-4F508B60', 'Angel''s Treat', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ ww. sk³adniki oprócz czekolady. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekorowaæ posypuj¹c czekolad¹ na wierzch kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184119-00001422C684065F-41FCE088', 'Arrowhead', 'Szklankê typu oldfashioned wype³niæ w 2/3 objêtoœci lodem t³uczonym, a nastêpnie wlaæ ww. sk³adniki i delikatnie zamieszaæ. Dekoracje wykonaæ z plasterka cytryny zawieszaj¹c go na krawêdzi szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184127-00001424B9BF6130-67CFE577', 'Bacardi Buck', 'Szklankê typu highball wype³niæ w 2/3 objêtoœci lodem, a nastêpnie wycisn¹æ po³ówkê cytryny. Dodaæ rum, Cointreau oraz uzupe³niæ zawartoœæ ginger ale. Dekoracje wykonaæ z plasterka cytryny zawieszaj¹c go na krawêdzi szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184135-000014268A317A15-FE121063', 'Bacardi Cocktail', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c plasterek limy na krawêdzi kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184145-0000142903F03E67-A2D152B5', 'Batidy', 'W mikserze przygotowaæ du¿o kruszonego lodu. Nastêpnie umieœciæ rum, cukier oraz ananasa. To wszystko zmiksowaæ na mus i przelaæ do kieliszka od wina. Serwowaæ ze s³omk¹ do picia (krótk¹).', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184153-0000142AD91DAE36-0883AA27', 'Beach Peach', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ rum, sok oraz peach brandy. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szklanki typu oldfashioned wype³nionej w 1/3 objêtoœci lodem kruszonym. Dekorowaæ wrzucaj¹c kawa³ek brzoskwini oraz zawieszaj¹c na krawêdzi wisienkê koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184202-0000142CDA2D0D18-16E2FDAF', 'Blue Hawaiian', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ wszystkie ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekoracje wykonaæ z po³¹czenia kawa³ka brzoskwini, wisienki koktajlowej oraz ananasa nabijaj¹c je na wspóln¹ wyka³aczkê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184210-0000142ECD7A985C-FCB9BCCF', 'Bolero', 'Do szklanki typu oldfashioned, wczeœniej wype³nionej trzema kostkami lodu wlaæ rum, wermut oraz calvados. Delikatnie zamieszaæ mieszade³kiem. Na koniec nad powierzchni¹ wycisn¹æ skórkê cytryny i wrzuciæ do œrodka szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184220-000014312B006AD5-9BF72F21', 'Caipirinha', 'Do szklanki typu oldfashioned, wrzuciæ pokrojon¹ na kawa³ki limê i cukier. Rozgniataæ do momentu kiedy cukier rozpuœci siê w soku limy. Dodaæ rum i kilka kostek lodu (wg uznania) i delikatnie wymieszaæ. Jako dekoracje na koniec zawiesiæ plasterek limy na krawêdzi szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184228-00001432FBBCA82F-912722A9', 'Canteloupe Cup', 'W mikserze umieœciæ rum, soki, melona oraz syrop cukrowy. Zasypaæ to garœci¹ kruszonego lodu i dobrze zmiksowaæ. Tak przyrz¹dzone przelaæ do kieliszka koktajlowego. Dekoracjê wykonaæ z plasterka melona ? zawiesiæ na krawêdzi lub wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184236-00001434DF72B92A-95ADDC9E', 'Capitain''s Cocktail', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ oba rumy i sok z limy. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szklanki typu oldfashioned. Na koniec wlaæ porto. Aby zrobiæ dekoracjê mo¿na pokusiæ siê o zawieszenie plasterka œwie¿ej limy na krawêdzi ?staroœwieckiej? szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184247-00001437443259B2-DB58DF4B', 'Caribbean Breeze', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ rum, soki i creme de banane. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka od wina w 2/3 objêtoœci wype³nionego wczeœniej lodem kruszonym. Aby zrobiæ dekoracjê mo¿na pokusiæ siê o zawieszenie plasterka œwie¿ej limy oraz kawa³eczka ananasa nadzianego na szpadtkê na krawêdzi eleganckiego kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184255-00001439472FF154-B9886C7A', 'Continental', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ rum, sok i creme de menthe. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Idealnie pasuje tutaj dekoracja z jednego œwie¿ego listka miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184303-0000143B03B856A5-EE003A25', 'Chop Nut', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ rum, sok i creme de cacao. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekoracjê wykonaæ ze spiralki skórki mandarynki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184310-0000143CB769F3AB-6E47B296', 'Cuba Libre', 'Na pocz¹tku do szklanki typu highball wrzuciæ t³uczony lód (ok. 1/2 objêtoœci). Wycisn¹æ nad powierzchni¹ sok z po³ówki limy i skórkê wrzuciæ do œrodka. Nastêpnie wlaæ rum i uzupe³niæ zawartoœæ zimn¹ coca?col¹. Dekoracjê wykonaæ ze spiralki skórki limy zawieszaj¹c j¹ w po³owie na krawêdzi, a w pozosta³¹ czêœæ wrzuciæ do szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184322-0000143F6A50825D-D1F0CB47', 'Columbus', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ rum, sok i brandy. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Dekoracjê wykonaæ z plasterka limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184329-00001441431A5986-C9572F84', 'Costa del Sol', 'Na pocz¹tku do szklanki typu highball wrzuciæ lód (ok. 1/2 objêtoœci). Nastêpnie wlaæ rum wermut, sok oraz syrop cukrowy. Wymieszaæ dobrze i uzupe³niæ zawartoœæ wod¹ gazowan¹ wg uznania. Dekoracjê wykonaæ z plasterka cytryny zawieszaj¹c go na krawêdzi szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184342-000014442ED1295C-1742452E', 'Daiquiri', 'Shaker wype³niæ w 1/2 objêtoœci lodem, a nastêpnie umieœciæ rum, wyciœniêty z po³ówki limy sok oraz cukier. Mieszaæ przez oko³o 5 sekund. Przecedziæ do wczeœniej odpowiednio sch³odzonego kieliszka koktajlowego. Nie wymaga dekorowania.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184349-00001445E6AB5254-768D1DD5', 'Diabolo', 'Shaker wype³niæ w 1/2 objêtoœci kruszonym lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do wczeœniej odpowiednio sch³odzonego kieliszka koktajlowego z odrobin¹ kruszonego lodu w œrodku. Na koniec aromatyzowaæ wyciskaj¹c skórkê cytryny nad powierzchni¹ kieliszka. Dekoracja: wrzuciæ spiralkê ze skórki cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184357-00001447C3D094A6-E5809CB0', 'El Presidente', 'Shaker wype³niæ w 2/3 objêtoœci kruszonym lodem, a nastêpnie umieœciæ rum, wermuty oraz grenadynê. Mieszaæ przez oko³o 5 sekund. Przecedziæ do szklanki typu oldfashioned w 2/3 wype³nionej kruszonym lodem. Na koniec dekorowaæ: wisienkê koktajlow¹ zawiesiæ na krawêdzi lekko nacinaj¹c, a plasterek pomarañczy wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184405-0000144972A44202-934E73C2', 'Emerald Star', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Przecedziæ do kieliszka koktajlowego. Nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184413-0000144B7591EDBE-9C6481A0', 'Eye of the tiger', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ z³oty rum, soki i syrop cukrowy. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do wype³nionej w 1/2 objêtoœci lodem szklanki typu highball. Na wierzch polaæ ciemnym rumem. Dekorowaæ zawieszaj¹c na krawêdzi szklanki plasterek pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184421-0000144D40B7FEBE-5B7323EF', 'Frozen Coconut', 'Kieliszek koktajlowy na pocz¹tku zanurzyæ w wiórkach kokosowych aby zosta³y na krawêdzi tworz¹c ozdobn¹ obwódkê. Nastêpnie wype³niæ w 1/3 kruszonym lodem. Sk³adniki umieœciæ w mikserze z garœci¹ kruszonego lodu. To wszystko zmiksowaæ i przelaæ do wczeœniej przygotowanego kieliszka koktajlowego. Serwowaæ ze s³omk¹. Konsumpcja: bezpoœrednio po podaniu.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184429-0000144F2BED4C64-E569A560', 'Goldilocks', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, soki i malibu. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do wype³nionej w 1/2 objêtoœci kruszonym lodem szklanki typu highball. Dekorowaæ zawieszaj¹c na krawêdzi szklanki plasterek pomarañczy oraz kawa³ek ananasa.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184437-00001451092613A3-61F024D8', 'Grenada', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, soki i malibu. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do wype³nionej w 1/2 objêtoœci kruszonym lodem szklanki typu highball. Dekorowaæ zawieszaj¹c na krawêdzi szklanki plasterek pomarañczy oraz kawa³ek ananasa.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184444-00001452A43FB5D2-5A420486', 'Havana Bandana', 'W mikserze z garœci¹ kruszonego lodu umieœciæ rum, sok z limy oraz jednego banana. Zmiksowaæ do stanu kiedy banan bêdzie idealnie p³ynny. Przelaæ do kieliszka koktajlowego, który wczeœniej zosta³ dobrze sch³odzony. Na koniec na wierzch walaæ creme de banane. Wed³ug uznania posypaæ ga³k¹ muszkatow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184453-0000145499674309-2CDF015C', 'Havana Beach', 'W mikserze z odrobin¹ kruszonego lodu umieœciæ rum, sok ananasowy oraz pokrojon¹ na kawa³ki limê. Miksowaæ do momentu osi¹gniêcia konsystencji musu. Przelaæ do szklanki typu oldfashioned wype³nionej w 1/3 objêtoœci lodem. Na koniec na wierzch walaæ ginger ale. Wed³ug uznania wkroiæ kawa³ki limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184501-0000145685123475-6DF5E07D', 'Hot Rum', 'Bezpoœrednio w szklance typu highball wymieszaæ ze sob¹ rum, cynamon, mas³o oraz cukier. Zawartoœæ uzupe³niæ gor¹c¹ wod¹. Jeszcze raz dobrze wymieszaæ. Podawaæ w szklance ¿aroodpornej. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184509-0000145855776B89-BD3EDF1B', 'Hot Toddy', 'Wszystkie ww. sk³adniki pomijaj¹c imbir podgrzaæ w specjalnie przeznaczonym do tego naczyniu, a nastêpnie przelaæ do naczynia ¿aroodpornego. Na koniec dodaæ imbir. Gotowe.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184517-0000145A4C812E4D-66CE94C4', 'Hurricane', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ wszystkie ww. sk³adniki poza dekoracj¹ (ananas, wisienka, pomarañcz). Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do wype³nionej w 1/2 objêtoœci lodem szklanki typu highball. Dekorowaæ zawieszaj¹c na krawêdzi szklanki plasterek pomarañczy, kawa³ek ananasa oraz wisienkê koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184524-0000145BF80A04AB-C33C2F69', 'Hustler', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ wszystkie ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do wype³nionego kilkoma kostkami lodu kieliszka do wina. Aromatyzowaæ skórk¹ limy i wrzuciæ j¹ do œrodka. Dekorowaæ zawieszaj¹c na krawêdzi szklanki plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184532-0000145DB9EA150C-1BF6F294', 'Jamaica Sunday', 'Na pocz¹tku w przeznaczonym do tego naczyniu rozpuœciæ miód w rumie. PóŸniej bezpoœrednio w szklance typu oldfashioned wsypaæ 1/2 objêtoœci kruszony lód, a na to przygotowany wczeœniej miód z rumem. PóŸniej sok z limy i jeszcze raz zamieszaæ. Zawartoœæ uzupe³niæ lemoniad¹ wed³ug uznania. Dekorowaæ plasterkiem limy zawieszonym na krawêdzi szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184539-0000145F54764287-E9320E2F', 'Kingston', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ wszystkie rum, d¿in oraz wyciœniêty sok z 1/2 limy. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do kieliszka koktajlowego. Aromatyzowaæ skórk¹ limy. Dekorowaæ zawieszaj¹c na krawêdzi kieliszka plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184547-000014613A580C02-5F48E811', 'Maison Charles', 'Wczeœniej rozgniecione kawa³ki miêty nale¿y zalaæ rumem i zostawiæ na ok. 15min. W tym czasie mo¿na wykonaæ dekoracjê dla kieliszka koktajlowego. Przeci¹gn¹æ brzeg kawa³kiem limy, a nastêpnie zanurzyæ w mieszance cukru pudru i delikatnie pociêtej listków miêty. PóŸniej w shaker wype³niæ w 1/3 lodem i wlaæ tam wczeœniej przygotowywany rum z miêt¹. Dodaæ sok z limy oraz syrop cukrowy. Mieszaæ przez ok. 5 sekund po czym odcedziæ do kieliszka koktajlowego (wczeœniej przygotowanego). Na koniec wg uznania posypaæ na wierzch listkami miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184555-000014634122582C-9E674459', 'Mai Tai', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ dwa rumy, soki oraz brandy. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do szklanki typu highball. Na koniec delikatnie wlaæ grenadynê. Dekorowaæ zawieszaj¹c na krawêdzi ananasa.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184603-00001464FD159823-4193C4A7', 'Mary Pickford', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ wszystkie rumy, sok, likier oraz grenadynê. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c na krawêdzi wisienkê koktajlow¹, któr¹ póŸniej mo¿na wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184610-00001466AD9FF417-331B89D9', 'Mojito', 'Na pocz¹tku w szklance typu highball rozgnieœæ listki miêty zalaæ sokiem z limy i zasypaæ t³uczonym lodem. Nastêpnie shaker wype³niæ w 1/3 objêtoœci lodem, umieœciæ rumy, sok, likier, wódkê gorzk¹ oraz syrop cukrowy. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do przygotowanej wczeœniej szklanki highball. Zawartoœæ uzupe³niæ wedle uznania wod¹ gazowan¹. Dekorowaæ zawieszaj¹c na krawêdzi plasterek cytryny, który póŸniej mo¿na wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184618-00001468697AD65B-D65F5FDE', 'Monkey Wrench', 'Wszystko wymaga przyrz¹dzenia bezpoœrednio w szklance typu highball. Wype³niæ j¹ w 1/3 objêtoœci lodem i wlaæ wszystkie ww. sk³adniki. Dobrze wymieszaæ. Dekorowaæ zawieszaj¹c kompozycjê z wisienki koktajlowej oraz plasterka grejpfruta.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184626-0000146A4E2EC077-149DC47B', 'Morning Joy', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, d¿in oraz sok. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do szklanki typu highball wype³nionej w 1/3 objêtoœci lodem. Dekorowaæ zawieszaj¹c na krawêdzi plasterek grejpfruta, którego póŸniej mo¿na wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184634-0000146C1E18DEEF-24A00C5A', 'Mulata', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, creme de cacao oraz sok. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c na krawêdzi plasterek limy, który póŸniej mo¿na wrzuciæ do œrodka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184642-0000146E08D1B7DD-F2DCC135', 'Northside Special', 'Pocz¹tkowo bezpoœrednio w szklance typu highball rozpuœciæ cukier wlewaj¹c i mieszaj¹c sok cytrynowy oraz pomarañczowy. Nastêpnie wype³niæ szklankê t³uczonym lodem i wlaæ rum oraz uzupe³niæ zawartoœæ wod¹ gazowan¹. Dekorowaæ zawieszaj¹c na krawêdzi plasterek pomarañczy i wisienkê koktajlow¹. Do œrodka wrzuciæ plasterek cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184649-0000146FCC2474B7-6BC6BF2E', 'Passion Punch', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, syrop oraz soki. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do wype³nionej w 1/3 objêtoœci szklanki typu oldfashioned. Dekorowaæ zawieszaj¹c na krawêdzi plasterek pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184657-000014719C1EC08F-7AB74F6A', 'Petite Fleur', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, Cointreau oraz sok. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c na krawêdzi plasterek grejpfruta.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184704-000014733541E71C-A0A2A712', 'Pilot Boat', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, creme de banane oraz sok. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c na krawêdzi plasterek cytryny .', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184712-000014751E073BEE-47191BBC', 'Pina Colada', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, krem kokosowy, cukier oraz sok. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c na krawêdzi plasterek ananasa skomponowany z wisienk¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184727-00001478A87B50DF-C4F68BE8', 'Sun City', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ ww. sk³adniki poza lemoniad¹. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do szklanki typu highaball w 1/3 wype³nionej lodem. Zawartoœæ uzupe³niæ lemoniad¹ wedle uzania. Aromatyzowaæ wyciskaj¹c nad powierzchni¹ skórkê limy. Dekorowaæ zawieszaj¹c na krawêdzi plasterek limy oraz cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184734-0000147A4AA6B8FF-7EF27835', 'San Juan', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie ww. sk³adniki poza ciemnym rumem. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do kieliszka koktajlowego. Na koniec skropiæ ciemnym rumem. Dekorowaæ zawieszaj¹c na krawêdzi plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184742-0000147C00D942D4-047D820E', 'Trade Winds', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, sok, syrop oraz œliwowicê. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c na krawêdzi plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184749-0000147DCC93A579-BFB4285F', 'Ti Punch', 'W szklance typu highball umieœciæ kawa³ki limy i pa³eczk¹ rozgnieœæ je a¿ puszcz¹ soki. Nastêpnie wlaæ rum oraz syrop i zasypaæ to lodem. Dekorowaæ zawieszaj¹c na krawêdzi plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184757-0000147FA3203648-C88A187B', 'Waikiki Beach', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ wszystkie ww. sk³adniki. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do kieliszka koktajlowego. Dekorowaæ zawieszaj¹c na krawêdzi wisienkê koktajlow¹', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184804-000014811C52894F-E1469DDD', 'Yum', 'Shaker wype³niæ w 1/3 objêtoœci lodem, a nastêpnie umieœciæ rum, Malibu oraz soki. Mieszaæ przez oko³o 5 sekund. Zawartoœæ przecedziæ do wype³nionej lodem w 2/3 objêtoœci szklanki typu highball. Dekorowaæ zawieszaj¹c na krawêdzi plasterek limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184902-0000148E9943A9AB-C2C6D389', 'Argo', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do kieliszka koktakjlowego. Dekoracja - utrzeæ gorzkiem czekolady.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184910-000014908EC371FD-823AD9BE', 'Bayou', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do szklanki typu oldfashioned z kilkoma kostakami lodu. Dekorowaæ plastrem brzoskwini.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184917-000014921F83F2CE-D1977145', 'B & B', 'Do kieliszka koktajlowego wlaæ pó³ na pó³ koniak oraz Benedictine. Niczym nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184926-000014942D492528-2609E5BB', 'Blackjack', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do kieliszka typu oldfashioned z kilkoma kostakami lodu. Nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184932-00001495BC6AABEE-40FAD40D', 'Bombay', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do kieliszka koktajlowego wypelnionego kruszonym lodem zostawiaj¹ kostki lódu w shakerze. Nie dekorowaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184939-000014973E96FF53-A3BCD0A8', 'Booster', 'Nape³niæ shaker lodem. Umieœciæ w nim wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do kieliszka koktajlowego. Na koniec, na wierzch kieliszka utrzeæ ga³ki muszkatowej.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184946-0000149900C332E0-A3906B18', 'Brandy Alexander', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do kieliszka koktajlowego zostawiaj¹ lód w shakerze. Dla jeszcze lepszego smaku mo¿na posypaæ ga³k¹ muszkatow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130184953-0000149AA3E3063D-1BBF6A25', 'Brandy Smash', 'W szklance typu oldfashioned rozpuœciæ cukier w bardzo ma³ej iloœci wody. W³o¿yæ lód, wlaæ koniak oraz wodê sodow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185000-0000149C41C56A38-01965F10', 'Brandy Fix', 'W szklance typu oldfashioned umieœciæ cukier. Wlaæ odrobinê wody i mieszaæ do rozpuszczenia. Nastêpnie wrzuciæ do szklanki t³uczony lód, wycisn¹æ sok z cytryny (ew. mo¿na wrzuciæ skórkê po wyciœniêtej cytrynie).', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185009-0000149E42DE9706-489C1E89', 'Captain Kidd', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do pucharka od szampana. Dekorowaæ plastrem pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185016-0000149FFBE830F3-F086C4AF', 'Hard Case', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do kieliszka koktajlowego zostawiaj¹ kostki lodu w shakerze. Dla jeszcze lepszego efektu - dekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185023-000014A1732C1906-81669191', 'Harvard', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do szklanki typu oldfashioned z kilkoma kostakami lodu. Dekorowaæ spiral¹ ze skórki cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185031-000014A373588D6C-13E78ADF', 'Incredible', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do szklanki typu oldfashioned z kilkoma kostakami lodu. Dekorowaæ wisienk¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185038-000014A515C59E61-E436E9BB', 'Kiss the Boys', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do szklanki typu oldfashioned wype³nionej kruszonym lodem. Dekorowaæ spiral¹ ze skórki cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185045-000014A6C48F0A25-9FDF9D16', 'Lemon Lady', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do kieliszka koktajlowego zostawiaj¹ kostki lodu w shakerze. Dla jeszcze lepszego efektu - dekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185052-000014A8569153A8-12310065', 'Torpedo', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki oraz energicznie wymieszaæ. Nastêpnie przelaæ do kieliszka koktajlowego. Dekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185059-000014A9F207B49A-5045A614', 'Truskawkowe Daiquiri', 'Zmiksowaæ truskawki w mikserze. Natêpnie wlaæ koniak, rum oraz sok z limy oraz du¿o kruszonego lodu po czym jeszcze raz wszystko zmiksowaæ. Przeleæ do kieliszka koktajlowego. Do przybrania wykorzystaæ ca³e truskawki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185107-000014ABCD5F6CF0-AF94CE8D', 'Vanderbilt', 'Nape³niæ shaker lodem. Umieœciæ w nim Wszystkie sk³adniki (poza angosutr¹) oraz energicznie wymieszaæ. Nastêpnie przelaæ do kieliszka koktajlowego zostawiaj¹ kostki lodu w shakerze. Dla jeszcze lepszego efektu - dekorowaæ angostur¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130185508-000014E3D97A3506-4B9BAA16', '8 Iron', 'Do kieliszka do wódki (shooters) wlaæ likier bananowy, delikatnie dolaæ (po œciance kieliszka) Blue Curacao na koñcu dolaæ Ouzo tak aby powsta³y trzy warstwy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190156-00001542FD16A51C-323B76C6', 'A.A. Collins', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ gin, Apricot Brandy, likier i sok cytrynowy, wstrz¹sn¹æ i razem z lodem przelaæ do szklanicy do longdrinków, dope³niæ wod¹ sodow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190213-00001546DB5A30AA-78F22397', 'Adria', 'Wszystkie sk³adniki, oprócz pomarañczowego Bitter wlaæ do shakera. Wymieszaæ i przelaæ do wysokiej szklanki. Dope³niæ pomarañczowym Bitter, wrzuciæ kostki lodu i lekko zamieszaæ. Na szklance po³ozyæ owoce kumkwatu nabite na szpadkê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190224-0000154965296CC9-40780E91', 'After Dinner Cocktail', 'Do shakera wrzuciæ kilka kostek lodu, wlaæ likiery i sok z limetki, energicznie wymieszaæ. Drink przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190233-0000154B8CC1A103-CBC4F7C5', 'Ajacoco', 'Brzegi kieliszka koktajlowego udekorowaæ tart¹ czekolad¹. Do shakera wrzuciæ kilka kostek lodu, wlaæ likier i mleko, energicznie wymieszaæ. Napój przecedziæ do kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190243-0000154DDEB40A0E-23535503', 'Aleksander', 'Do shakera wrzuciæ kostki lodu. Wlaæ koniak, likier i œmietankê. Energicznie potrz¹sn¹æ, aby sk³adniki utworzy³y jednorodna masê. Do kieliszka koktajlowego wrzuciæ kostki lodu i wlaæ napój przez sitko. Wierzch koktajlu posypaæ start¹ ga³k¹ muszkato³ow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190252-0000154FE428E7DB-21C74C55', 'Amaretto and Cream', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ likier i œmietankê, dobrze wstrz¹sn¹æ, przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190300-00001551DC45D760-3ABBBA7F', 'Amaretto Sour', 'Do shakera wrzuciæ kilka kostek pokruszonego lodu. Wlaæ likier i sok cytrynowy, dobrze wstrz¹sn¹æ, przecedziæ do szklanki typu collins. Przybraæ plasterkiem pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190310-0000155424982D42-89E858F5', 'Amaretto Stinger', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ likier Amaretto i Creme de Menthe, dobrze wstrz¹sn¹æ, przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190322-00001556D33E565B-59A477FA', 'American Amaretto', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Wlaæ Amaretto, whisky i Creme de Menthe, zamieszaæ i przecedziæ do kieliszka koktajlowego. W³o¿yæ 1/4 plastra pomarañczy i przybraæ ga³¹zk¹ miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190330-00001558ADE5CF0B-8A48BC56', 'American Beauty', 'Szklankê do longdrinków nape³niæ do po³owy t³uczonym lodem, wlaæ wszystkie sk³adniki i krótko zamieszaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190339-0000155AEE21F31F-65BAB9FF', 'Angel''s Kiss', 'Szklankê typu tumbler wype³niæ lodem. Wlaæ likier Cointreau i soki, uzupe³niæ wod¹ sodow¹. Udekorowaæ listkiem miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190348-0000155D0968CF4A-EFE5EDBD', 'Apple Sunrise', 'Szklankê do longdrinków nape³niæ do po³owy t³uczonym lodem. Wlaæ najpierw calvados, a nastêpnie sok cytrynowy i Creme de Cassis. Dope³niæ sokiem pomarañczowym i lekko zamieszaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190359-0000155F89E27A91-62D26C23', 'Apricot Blossom', 'Do szkalanicy barmañskiej wrzuciæ kilka kostek lodu. Wlaæ gin i likier, zamieszaæ. Do p³askiego kieliszka koktajlowego wsypaæ nieco t³uczonego lodu i przecedziæ koktajl. W³o¿yæ wiœniê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190408-00001561A66C5672-D1D2B13B', 'B-52', 'Do kieliszka (shot glass) wlaæ Kahule, potem delikatnie (najlepiej po brzegu kieliszka) Baileysa i Grand Marnier (tak aby powsta³y 3 warstwy). Na koniec wlaæ spirytus i podpaliæ. Piæ przez s³omkê na raz wsadzaj¹c j¹ na same dno.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190416-0000156370683061-17F9F0B1', 'B-52 Fire', 'Sch³odziæ kieliszek koktailowy. Wlaæ po ³y¿eczce khalue, nastepnie baileysa a na wierzch cointreau. S³omkê zamoczyæ w wodzie. Drinka podpaliæ i w³o¿yæ s³omke, piæ jednym poci¹gniêciem a¿ p³omieñ zniknie w s³omce.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190428-000015664BAA3678-2058B84F', 'Baby Baby', 'Do szklanki typu tumbler wrzuciæ kilka kostek lodu, wlaæ wódkê, likier Cointreau i sok pomarañczowy. Zamieszaæ i dodaæ plasterek cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130190436-00001568354A4F44-45E3DE3E', 'Back in Black', 'Do szklanki do long drinków wrzuciæ kilka kostek lodu. Wlaæ tequilê i likier. Dope³niæ coca col¹, zamieszaæ i udekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191508-000015FB5CA48547-665DB1E3', 'Bahama Star', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ rum, likier sok bananowy i pomarañczowy, wstrz¹sn¹æ. Do szklanicy do longdrinków wrzuciæ trochê t³uczonego lodu i przecedziæ drink. Wycisn¹æ sok z limetki i dodaæ do napoju. Przybraæ ga³¹zk¹ miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191534-00001601734B4903-80A66C05', 'Banana Daiquiri', 'Wszystkie sk³adniki wraz z t³uczony lodem wrzuciæ do elektrycznego miksera i zmiksowaæ. Wlaæ do kieliszka balonowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191547-000016044A6C13C7-B8114B16', 'Bananowy', 'Do szkalnicy barmañskiej wlaæ likiery, Advocata i mleko zagêszczone. Wszystko zamieszaæ i przelaæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191556-000016065CA91BB2-D7BD9CE7', 'Bella Gioia', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ wszystkie sk³adniki, wstrz¹sn¹æ i przecedziæ do p³askiej czarki koktajlowej.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191604-000016085E8CCB76-0A164D7D', 'Black Jack', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ likiery i whisky, dobrze wymieszaæ, przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191618-0000160B8217370D-10138002', 'Black Pearl', 'Kilka kostek lodu w³o¿yæ do shakera, wlaæ likier Tia Maria i brandy. Dok³adnie wymieszaæ. Koktajl przelaæ przez sito do wysokiego kieliszka do szampana. Drink dope³niæ lodowatym szampanem i udekorowaæ wiœni¹ koktajlow¹ nabit¹ na ma³y szpikulec.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191627-0000160DA197D49F-A804DC88', 'Black Russian', 'Do szkalnki typu old fashioned wrzycamy kilka kostek lodu. Wlewamy wódkê i likier, dok³adnie mieszamy mieszade³kiem i serwujemy, mo¿na podawaæ ze s³omk¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191648-000016127C18E81A-834DABA6', 'Black Russian II', 'Do wysokiej szklanki wsypaæ kilka kostek lodu, wlaæ likier i wódkê, uzupe³niæ coca-col¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191657-000016149BF54FFD-AEBD4E04', 'Blue Angel', 'Do shakera wrzuciæ kilka kostek lodu. Dodaæ wszystkie sk³adniki, dobrze wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191705-0000161678B4C03C-BAE7D6FC', 'Blue Laguna', 'Wódkê i likier wlaæ do szklanki koktajlowej, uzupe³niæ lemoniad¹ i zamieszaæ. Dodaæ kilka kostek lodu. Brzeg szklanki udekorowaæ plasterkiem cytryny. Podawaæ ze s³omk¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191712-0000161844E8E2A4-DF0AC4B0', 'Blue Splash', 'Do wysokiej szklanki wrzuciæ kostki lodu, wlaæ sk³adniki, delikatnie zamieszaæ mieszade³kiem do drinków. Udekorowaæ wiœniami koktajlowymi i cz¹stkami ananasa.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191722-0000161A84CBC6CD-C88829C1', 'Bobby Burns', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Wlaæ likier, wermuty i whisky, zamieszaæ i przecedziæ do kieliszka koktajlowego. Skropiæ sokiem wyciœniêtym ze skórki cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191731-0000161C8E71C0E8-EDAFA489', 'Bobby Fitz', 'Do shakera wrzuciæ kilka kostek lodu. Dodaæ whisky, Pastis, likier pomarañczowy, Maraschino oraz Angosturê, wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego. Przybraæ plastrem pomarañczy i oliwk¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191738-0000161E511FDE01-0B398DE0', 'Bonnie''s Love', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ likier, sok pomarañczowy i dobrze wstrz¹sn¹æ. Przecedziæ przez sitko do kieliszka koktajlowego i dope³niæ dobrze oziêbionym winem musuj¹cym. Skórkê pomarañczow¹ skroiæ w spiralê i przybraæ koktajl.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191747-00001620697C73B8-9CFAF3C3', 'Brandy Cassis', 'Do shakera wrzuciæ kilka kostek pokruszonego lodu. Wlaæ brandy, sok cytrynowy i Creme de Cassis, energicznie wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego. Przybraæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191755-000016222CF9F9A6-4F248497', 'Brandy Crusta', 'Brzegi szampanki zwil¿yæ sokiem cytrynowym i zanurzyæ w cukrze. W³o¿yæ do kieliszka skórkê cytrynow¹. Do shakera wrzuciæ kostki lodu, dodaæ pozosta³e sk³adniki, dobrze wstrz¹sn¹æ i przecedziæ do szampanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191940-0000163ABE20508E-1582C62D', 'Cafe Creme', 'Do wysokiej szklanki wrzuæ kilka kostek lodu. Wszystkie sk³adniki przelej do shaker’a z lodem. Wymieszaj i przelej zawartoœæ do szklanki. Górê posyp czekolad¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130191948-0000163C8E0C2A15-1796B2D1', 'Campari Punch', 'Wysok¹ szklankê z kilkoma kostkami lodu nape³niæ przygotowanymi sk³adnikami, zamieszaæ. Udekorowaæ plasterkami pomarañczy i ogórka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192003-0000163FF61B9A58-05A103EB', 'Canadian Squash', 'Do shakera wrzuciæ kilka kostek lodu. Dodaæ wszystkie sk³adniki, wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192012-000016421CBECCBC-2D6F7343', 'Capitano', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ wszystkie sk³adniki i wstrz¹sn¹æ. Przecedziæ do du¿ej szklanki do longdrinków z du¿¹ iloœci¹ t³uczonego lodu.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192027-000016459007A671-A4E1C279', 'Caribbean Champangne', 'Rum i likier bananowy wlaæ do p³askiej szampanki, dope³niæ szampanem i w³o¿yæ plaster babana.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192036-00001647A1441100-E27EC731', 'Caribic Queen', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ wszystkie sk³adniki, wstrz¹sn¹æ i przecedziæ do p³askiej szampanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192045-00001649BE46F894-ACF78506', 'Casablanca', 'Rum, likiery i sok z cytryny wlaæ do shakera. Energicznie wymieszaæ. Napój przelaæ do kieliszka koktajlowego, dodaæ 2 krople angostury.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192059-0000164CF8DFA83A-1D458BEA', 'Champagner Cobbler', 'Kieliszek do cobbleru lub czerwonego wina nape³niæ 3/4 wysokoœci t³uczonym lodem. Wlaæ likier pomarañczowy, Maraschino i koniak, dope³niæ szampanem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192107-0000164EF141FA58-A6699114', 'CoCo Bongo', 'Malibu, Cointreau, i sok z 1/2 wyciœniêtej Limonki wlaæ do shakera. Zamieszaæ i przelaæ do Long glass wype³nionej lodem i plasterkami limonki. Dope³niæ Spritem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192119-000016519A86772B-D40C1911', 'Cointreau Caipirinha', 'Szklankê typu tumbler wype³niæ kruszonym lodem, limetke poci¹æ na 5-6 kawa³ków, czêœciowo wycisn¹æ sok i wrzuciæ do szklanki. Wlaæ likier Cointreau i zamieszaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192126-0000165369636DC3-31C4AAB3', 'Cointreau Clip', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ likier Cointreau i sok z limetki, wymieszaæ. Przelaæ do szklanki do longdrinków, dope³niæ sokiem grapefruitowym i zamieszaæ. Wrzuciæ plasterek limetki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192135-0000165549EDAE04-5D59EB5C', 'Cointreau Sparkle', 'Do kieliszka do szampana wlaæ likier Cointreau i dope³niæ dobrze sch³odzonym bia³ym winem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192143-000016574C192495-5DB90677', 'Cointreau Tonic', 'Szklankê typu tumbler wype³niæ lodem. Wlaæ likier Cointreau i uzupe³niæ tonikiem, zamieszaæ. Udekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192209-0000165D54470AAA-740C131F', 'Creme Ol’e', 'Kieliszek koktajlowy ukoronuj czekolad¹. Wódkê, CREME i mleko wlej do shaker’a z lodem. Wymieszaj i przelej zawartoœæ do kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192222-000016603E8AF431-0D83231F', 'Crescent Moon Tequila', 'Tequile, sok z cytryny i wodê mieszamy w shakerze, przelewamy do szklanki z lodem. Dodajemy delikatnie curacao. Zdobimy po³ow¹ plasterka cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192232-000016629FDB9E4F-4CA01BE7', 'Daiquiri Cocktail No.3', 'Do shakera wrzuciæ kilka kostek lodu, wlaæ wszystkie sk³adniki, wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192240-000016647FCBEE9F-157A38FF', 'Daisy', 'Do shakera w³o¿yæ trzy rozdrobnione kostki lodu. Wlaæ syrop grenadine, sok cytrynowy i likier Chartreuse. Energicznie potrz¹sn¹æ kilkanaœcie razy. Koktajl przelaæ przez sitko do du¿ego p³askiego kieliszka do szampana. Dope³niæ szampanem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192246-00001665FAEDA450-120A5ED5', 'Dirty Job', 'Do wysokiej szklanki w³o¿yæ kilka kostek lodu, nastêpnie wlaæ wódkê i likier, zamieszaæ. Dope³niæ tonikiem i udekorowaæ spiralk¹ ze skórki pomarañczowej.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192255-000016680E97425E-15C134AB', 'Dirty White Mother', 'Œmietankê, koniak i likier kawowy z kostkami lodu energicznie wymieszaæ w shakerze, koktajl przelaæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192304-0000166A063A6964-ED8B3667', 'Dlaczego Nie', 'Do wysokiej szklanki w³o¿yæ kilka kostek lodu, nastêpnie wlaæ wszystkie sk³adniki, wymieszaæ. Szklankê udekorowaæ spiralk¹ z cytryny i ga³¹¿k¹ miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192312-0000166BDFAF34D8-BDC6B1AC', 'Drambuie Cocktail', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Dodaæ Drambuie, wermut i gin, krótko zamieszaæ i przecedziæ do czarki koktajlowej lub p³askiej szampanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192321-0000166DFEC9C33B-80F37713', 'Duke Cocktail', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ Curacao, soki, Maraschino, dodaæ jajko, dobrze wstrz¹sn¹æ i przecedziæ do du¿ej szampanki. Dope³niæ szampanem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192330-0000167011E9B25B-2AB59024', 'Eliza', 'Wszystkie sk³adniki, oprócz wody mineralnej, wlaæ do shakera i dobrze wymieszaæ. Przygotowaæ wysok¹ szklankê z kilkoma kostkami lodu, wlaæ przygotowanego drinka, dope³niæ wod¹ mineraln¹. Udekorowaæ plasterkami cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192337-00001671C918D363-ADF46B1D', 'Exotic Refreshment', 'Do szkalnki do longdrinków w³o¿yæ kilka kostek lodu. Wlaæ likier, wódkê i soki, zamieszaæ. Przybraæ kawa³kiem ananasa, miêt¹ i wiœni¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192345-00001673BE3AB41F-8E427E1F', 'Family Philip''s', 'Wymieszaæ wszystkie sk³adniki w shakerze i przelaæ do wysokiej szklanki wype³nionej w po³owie kruszonym lodem, udekorowaæ ananasem i pomarañcz¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192411-00001679C6380846-1867677C', 'Ferrari Cocktail', 'Do shakera wrzuciæ kilka kostek lodu. Dodaæ wszystkie sk³adniki, wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192420-0000167BE550B797-2A9F4F85', 'Filnpass', 'Do shakera wrzuciæ 4 kostki lodu wlaæ wszystkie sk³adniki, wymieszaæ i przelaæ do wysokiej szklanki drinkowej , Plastry z pomarañczy nabiæ na metalowe mieszade³ko i po³o¿yæ wzd³u¿ na szklance.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192429-0000167DE3967FE0-E6BAE738', 'French Connection', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Dodaæ likier i koniak, zamieszaæ i przecedziæ do ma³ego kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192439-0000168022BC2749-B3BA6A76', 'Frozen Margarita', 'Brzegi kieliszka balonowego zwil¿yæ sokiem cytrynowym, po czym zanurzyæ w soli. Do miksera wlaæ tequilê, likier pomarañczowy, sok cytrynowy, dodaæ cukier i 1 fili¿ankê t³uczonego lodu. Zmiksowaæ i przelaæ do kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192449-000016828FF2144F-A822F4D4', 'Godfather', 'Wszystkie sk³adniki wlej do szklanki typu “whiskaczówka” wczeœniej wype³nionej lodem i delikatnie zamieszaj.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192457-000016845940ADC2-6EF156BE', 'Golden Cadillac', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ Creme de Cacao, likier Galliano i œmietankê. Dobrze wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192505-000016863E511CCA-6F6B9799', 'Grasshopper', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ Creme de Cacao, Creme de Menthe i œmietankê. Dobrze wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego. Przybraæ ga³¹zk¹ miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192512-00001687D1FECB36-CDA5B9C4', 'Green Coconut', 'Do shaker wrzuciæ kilka kostek lodu. Wlaæ sk³adniki i wstrz¹sn¹æ. Kieliszek balonowy nape³niæ do po³owy t³uczonym lodem i przecedziæ koktajl.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192520-00001689B674E92E-42649FE6', 'Green Daiquiri', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ wszystkie sk³adniki i dobrze wstrz¹sn¹æ. Przecedziæ do kieliszka koktajlowego, wype³nionego t³uczonym lodem. W³o¿yæ plastrem limy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192528-0000168B9E479000-F1468549', 'Green Devil', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ gin, Creme de Menthe i sok z limy, wstrz¹sn¹æ i przelaæ razem z lodem do szklaneczki do whisky. Przybraæ ga³¹zk¹ miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192535-0000168D502C6ABE-4264DBE3', 'Green Emma', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ gin, Amaretto, Curacao, sok pomarañczowy i bia³ko. Wszystko dobrze wstrz¹sn¹æ. Przecedziæ do szklanki do long drinków, dope³niæ winem musuj¹cym i przybraæ miêt¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192543-0000168F32C1C51A-F0B55B3F', 'Green Frog', 'Do wysokiej szklanki w³o¿yæ lód, nastêpnie wlaæ wódkê, póŸniej dolaæ Curacao, dope³niæ sokiem pomarañczowym. Dobrze zamieszaæ. Podawaæ ze s³omk¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192819-000016B362EBA05C-53883D9D', 'Harry''s Eyes', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ sk³adniki i dobrze wymieszaæ. Przelaæ razem z lodem do szklaneczki do whisky.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192837-000016B7C4F61B92-A5E124D9', 'Harvey Wallbanger', 'Do szklanicy do longdrinków wrzuciæ 3-4 kostki lodu. Wlaæ wódkê i sok pomarañczowy, zamieszaæ, polaæ likierem, przybraæ wiœni¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192851-000016BB02D13C68-1C1BA994', 'Hawajski', 'Wszystkie sk³adniki, oprócz wody mineralnej, wymieszaæ w shakerze. Przygotowaæ wysok¹ szkalnkê z kilkoma kostkami lodu, wlaæ przygotowanego drinka i dope³niæ wod¹ sodow¹. Udekorowaæ kawa³kami ananasa, czereœni¹ z syropu i listkami miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192901-000016BD3905013A-9429F948', 'Hemingway', 'Do shakera wrzuciæ fili¿ankê drobno t³uczonego lodu. Wlaæ sk³adniki, wstrz¹sn¹æ i przelaæ do du¿ej szampanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192909-000016BF074E8E9D-5BF8AB64', 'Highland Dream', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ gin, likier, sok pomarañczowy, skropiæ angostur¹ i dobrze wstrz¹sn¹æ. Przecedziæ do p³askiej szampanki i dope³niæ winem musuj¹cym.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192917-000016C0E39AE8C0-8F56BD9B', 'Honeymoon', 'Œmietankê, koniak i likier kawowy z kostkami lodu energicznie wymieszaæ w shakerze, koktajl przelaæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192925-000016C2BE015A1D-02B22951', 'Ideal Cocktail', 'Do shakera wrzuciæ kilka kostek lodu, dodaæ wszystkie sk³adniki, dobrze wstrz¹sn¹c i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192933-000016C4B09D40E3-75E724CA', 'Jade', 'Wszystko mieszamu razem w shakerze wlewamy do schlodzonych koktajlowych kieliszkow i przyozdabiamy plasterkiem limonki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192941-000016C6982F729E-02E08A7F', 'Jade II', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ rum Curacao, likier miêtowy i dobrze wstrz¹sn¹æ. Przecedziæ do kieliszka koktajlowego i dope³niæ winem musuj¹cym.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192949-000016C87E47FC5F-E9D48E46', 'Jungle Bird', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ tequilê, likier i soki, wstrz¹sn¹æ i przecedziæ do p³askiej szampanki. Przybraæ koktajl plastrem pomarañczy i wiœni¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130192957-000016CA3D550615-C2AFD8DE', 'Kamikazzee Blue Dj', 'Wszystkie sk³adniki zmiksowaæ shekerem, podawaæ w kieliszkach koktajlowych.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193007-000016CC8B4829F3-5D409FB0', 'Kastaniety', 'Do shakera wrzucamy lód i pozosta³e sk³adniki, d³ugo wstrz¹samy i odcedzamy do 6-ciu kieliszków.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193016-000016CECF3963D5-A2BE2C1A', 'Kir Royal', 'Creme de Cassis wlaæ do wysokiego kieliszka, dope³niæ mocno sch³odzonym szampanem lub winem musuj¹cym. Drink jest aperitifem, podaje siê go przed posi³kiem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193025-000016D0C9123ABF-6BC26E32', 'Klabautermann', 'Do shakera wrzuciæ kilka kostek lodu. Dodaæ wszystkie sk³adniki, wstrz¹sn¹æ i przecedziæ do szklaneczki do whisky, w której znajduj¹ siê 2 kostki lodu.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193034-000016D302F9FCEA-9F33D87B', 'Krwawy Mózg', 'Do kieliszka wlaæ wódkê brzoskwiniow¹, dodaæ kilka kropel Baileys i dodaæ kilka kropel Grenadine.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193042-000016D4AA7BCE2D-84251567', 'La Jolla', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ brandy, Creme de Banana, sok pomarañczowy i sok cytrynowy, dobrze wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193049-000016D6776A93A9-DAD79AC4', 'Lady Be Good', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ brandy, Creme de Menthe i wermut, dobrze wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193059-000016D89AC2F59C-E3337A9F', 'Lady Störtebeker', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Wlaæ gin, Creme de Cassis i Amaretto, zamieszaæ, przecedziæ do p³askiej szampanki i dope³niæ winem musuj¹cym. Skórkê pomarzñczy skroiæ w cienk¹ spirelê i przybraæ koktajl.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193107-000016DA958B429D-26CE6A6C', 'Lazarac-Cocktail', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ grenadynê, Orange Bitters, angosturê, Pastis, likier any¿owy i whisky, wstrz¹sn¹æ i przelaæ razem z lodem do szklaneczki. Skropiæ sokiem ze skórki cytryny, a skórkê w³o¿yæ do szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193115-000016DC80FF793A-746AAFF0', 'Legend', 'Sk³adniki wlaæ do kieliszka koktajlowego , dope³niæ sokiem ananasowym i zamieszaæ, ewentualnie dodaæ kostkê lodu.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193123-000016DE62FB07A7-403D1D94', 'Magic', 'Wódkê, likier syrop truskawkowy i sok wlaæ do shakera, wymieszaæ, przelaæ do szklanicy do longdrinków, uzupe³niæ tonikiem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193132-000016E06E311FA2-B93A6AF9', 'Mah-Jong', 'Wszystkie alkohole wlaæ do shakera, energicznie wymieszaæ. Do szklanki old fashioned wrzuciæ kilka kostek lodu i przelaæ napój z shakera.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193205-000016E82FF4125C-48BF1E37', 'Man Club', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ gin, likier i soki, wstrz¹sn¹æ i przelaæ razem z lodem do szklanicy do longdrinków. W³o¿yæ wiœnie.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193221-000016EBEBF93FBF-C616EB62', 'Masquerade', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ Southern Comfort, sok cytrynowy i grenadynê, wstrz¹sn¹æ. Do szklanicy do longdrinków wrzuciæ 2-3 kostki lodu, przecedziæ drink i dope³niæ niewielk¹ iloœci¹ wody mineralnej. Plaster pomarañczy przekroiæ na pó³, nabiæ na mieszade³ko razem z wiœniami i w³o¿yæ do szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193230-000016EDE42531A0-577667FD', 'Menthe Martini', 'Jedn¹ kostkê lodu w³o¿yæ do kieliszka. 2-3 kostki wrzuciæ do szklanki, wlaæ likier wermut i napój, zamieszaæ ³y¿k¹ barow¹, przelaæ przez sitko do kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193238-000016EFE17DBFA5-F3DBA9B2', 'Monte Carlo Highball', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ sok z cytryny, gin, Creme de Menthe, wsypaæ cukier i wszystko dobrze wstrz¹sn¹æ. Przelaæ do szklanki do long drinków, w³o¿yæ 2 kostki lodu i dope³niæ szampanem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193247-000016F1E5759BBD-960EBA91', 'Monza', 'Do shakera wlaæ wódkê, likier i sok. Wstrz¹sn¹æ i przelaæ do szklanki old fashioned z kruszonym lodem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193255-000016F39E2DA54E-8AFC0997', 'Niebieskie Migda³y', 'Do shakera wlaæ likier, mleko i sok ananasowy, wstrz¹sn¹æ. Przelaæ do kieliszka koktajlowego wype³nionego du¿¹ iloœci¹ pokruszonego lodu. Napój przybraæ wiœni¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193306-000016F648AB0559-AEF25C27', 'Ohio', 'Do kubka mikserowego w³o¿yæ kilka kostek lodu. Wlaæ Angosturê, Curacao, likier Maraschino i whisky, wymieszaæ, przelaæ do kieliszka koktajlowego przez sitko. Dolaæ wino musuj¹ce. Plasterek pomarañczy przeci¿æ na pó³ i w³o¿yæ do koktajlu razem z wiœni¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193315-000016F8746E7CA7-59B7F4BF', 'Opener Eyes Flip', 'Do shakera wrzuciæ kilka kostek lodu. Dodaæ wszystkie sk³adniki, dobrze wstrz¹sn¹æ i przecedziæ przez sitko do kieliszków do flipów.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193325-000016FA98E98B36-162BAA6A', 'Orange-Flip', 'Lód pokruszyæ na drobne kawa³ki, w³o¿yæ do shakera, wlaæ wszystkie sk³adniki, dobrze wymieszaæ. Przelaæ przez sitko do wysokiego kielicha. Drink jest aperitifem, podaje siê go przed posi³kiem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193418-00001706F4EEB048-64D78FFF', 'Quiet Sunday', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ wódkê, Amaretto, sok pomarañczowy i wstrz¹sn¹æ. Przecedziæ do kieliszka koktajlowego i dodaæ grenadynê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193434-0000170AB651F9A4-489CEFC9', 'Red Sunshine', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ gin, cassis, sok cytrynowy i wstrz¹sn¹æ. Przecedziæ do szklanicy do longdrinków, dodaæ 2 kostki lodu i dope³niæ niwielk¹ iloœci¹ wody sodowej. Przybraæ plastrem limonki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193447-0000170DCECA7C96-F291F977', 'Ritz', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ likier, sok i syrop pomarañczowy, dobrze wstrz¹sn¹æ i przecedziæ do szampanki. Dope³niæ winem musuj¹cym.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193519-00001715501316F7-BF5B1CC9', 'Roman', 'Maliny przebraæ i przetrzeæ. Do shakera wrzuciæ kilka kostek lodu. Dodaæ przetarte maliny i likier, dobrze wstrz¹sn¹æ. Przecedziæ przez sitko do szampanki i dope³niæ winem musuj¹cym.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193528-0000171758FCA292-23CB1C34', 'Russian Coffe', 'Kilka kostek lodu w³o¿yæ do shakera, wlaæ wódkê i likiery. Wymieszaæ i przecedziæ do kieliszków koktajlowych. Przybraæ wisienk¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193548-0000171BFEDD01CF-902D8617', 'Scorpion Lady', 'Rum, brandy, Amaretto, sok cytrynowy i pomarañczowy wlaæ do miksera elektrycznego, wsypaæ fili¿ankê drobno t³uczonego lodu i zmiksowaæ. Wlaæ do du¿ej szampanki i przybraæ listkiem melisy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193606-000017201E769108-D2477CDC', 'Screaming Orgasm', 'Wlaæ alkohole do shakera wype³nionego kostkami lodu, nastepnie wymieszaæ. Przecedziæ przez sito shakera lub sitko barmañskie wlewajac do szklanki wype³nionej kilkoma kostkami lodu. Podawaæ z maraskami czyli wisienkami koktajlowymi powieszonymi na brzegu szklanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193616-00001722756A7291-42720A42', 'See Voo', 'Wszystkie sk³adniki shakerujemy oprócz sprite, nastêpnie wlewamy ca³¹ zawartoœæ do szk³a typu hurricaine wype³nionego kruszonym lodem wraz z kawa³kami truskawek i borówek a na koniec dolewamy sprite.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193624-0000172460AC9C93-9BA13F4A', 'Shark''s Tooth', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ Creme de Cassis Boudier, likier i wermut, dobrze wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego. Dope³niæ niewielk¹ iloœci¹ toniku.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193633-000017266780CFC2-A4928904', 'Side Car', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ Cognac, likier i sok cytrynowy. Wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193641-0000172873D8A55C-18B797E8', 'Skittles', 'Do szklanki Long-drink wrzuciæ kilka kostek lodu, do Shakeer''a wrzuciæ równie¿ kilka kostek lodu i dolaæ Curaco, sok malinowy i syrop brzoskwiniowy. Wstrz¹sn¹æ i przelaæ do szklanki, dope³niæ Fant¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193650-0000172A80A3F68E-7C3D1E64', 'Snow Ball', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ wódkê, Amaretto i œmietankê, dobrze wstrz¹sn¹æ i przecedziæ do szklanki old fashion. Oprószyæ odrobin¹ ga³ki muszkato³owej.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193657-0000172C224A530E-155EC844', 'Soft Lady', 'Do miksera wrzuciæ kilka kostek lodu. Wlaæ gin, likier i sok cytrynowy, zamieszaæ. Do szklaneczki do whisky wrzuciæ 2-3 kostki lodu i przecedziæ koktajl. Przybraæ plastrem pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193705-0000172DDA0B04C6-6FD48BC3', 'Son Of A Peach', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ gin, likier i sok pomarañczowy, wstrz¹sn¹æ i przecedziæ do szklanki typu tumbler. Dope³niæ wod¹ sodow¹, przybraæ plastrem pomarañczy i listkiem miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193713-0000172FE7585557-8228FA35', 'Southern Sunrise', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ likier, grenadynê i sok cytrynowy, wstrz¹sn¹æ. Przecedziæ do kieliszka koktajlowego, dope³niæ sokiem pomarañczowym. Przybraæ plastrem pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193721-00001731A3F55A39-6DA8F43F', 'Southern Sunset', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ wszystkie sk³adniki, wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193729-0000173395977A08-0C0D4A23', 'Stinger', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ brandy i likier, wstrz¹sn¹æ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193741-000017363C996A9A-CFF395AB', 'Sunny', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ gin, rum, likier soki oraz grenadynê i wstrz¹sn¹æ. Razem z lodem przelaæ do szklanicy do longdrinkow. Przybraæ plastrem pomarañczy, wiœni¹ i melis¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193752-00001738C6D0D0FF-A75733E8', 'Sweet Maria', 'Wszystkie sk³adniki wlaæ do shakera, wrzuciæ kostki lodu. Energicznie wstrz¹sn¹æ kilka razy, przelaæ do kieliszka koktajlowego przez sitko.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193800-0000173AD4ECCEB2-D4B593F7', 'Sweet Melon', 'Wszystkie sk³adniki dobrze wstrz¹sn¹æ w shekerze i przelaæ do szklanki wype³nionej lodem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193808-0000173CA654D0C6-D67ABA0F', 'Swing', 'Rum, likier i sok z cytryny wlaæ do shakera, dodaæ rozkruszone kostki lodu. Ca³oœæ dobrze wstrz¹sn¹c. Przelaæ do kieliszka przecedzaj¹æ przez gêste sitko.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193853-00001746F7D6FE1F-ACEF1941', 'Tallyman''s Drink', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ sk³adniki, wstrz¹sn¹æ, przecedziæ do szklaneczki do whisky, przybraæ plastrem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193902-000017491B7EB03D-21759581', 'Tampico', 'Do szklanki do longdrinków wlaæ Campari, Coimtreau i sok cytrynowy. Zamieszaæ i uzupe³niæ tonikiem. Dodaæ plasterek pomarañczy i skórkê pomarañczow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193931-0000174FDD2408DD-3FC74204', 'Tipperary Cocktail', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Dodaæ whisky, likier i wermut, zamieszaæ i przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193938-000017518C2B4869-E84D0ED0', 'Tornado', 'Sk³adniki wlaæ do shakera, dodaæ kilka kostek lodu dok³adnie wymieszaæ. Przelaæ do wysokiej szkalnki przez sitko, do ka¿dej dodaæ wiœniê nadzian¹ na szpadkê i listek miêty.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193950-000017545842B457-A2D75726', 'Tropical Cream', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ Pisang Ambon, rum, sok, œmietankê i dobrze wstrz¹sn¹æ. Przecedziæ do kieliszka koktajlowego i przybraæ miêt¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130193959-0000175687F53545-F29645CE', 'Up to Date', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Wlaæ whisky, sherry, likier pomarañczowy i angosturê, zamieszaæ i przecedziæ do kieliszka koktajlowego. W³o¿yæ wiœniê koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194014-00001759D6F9F7E3-51CC038D', 'Vermouth-Cassis', 'Do kieliszka koktajlowego wrzuciæ 2-3 kostki lodu. Wlaæ wermut i Creme de Cassis, zamieszaæ, skropiæ wyciœniêt¹ skórk¹ cytrynow¹ i dope³niæ wod¹ sodow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194024-0000175C29ADE0F3-D55A05FE', 'Welcome', 'Do shakera wrzuciæ kilka kostek. Wlaæ wszystkie sk³adniki, dobrze wstrz¹sn¹æ i przecedziæ do ma³ego kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194031-0000175DF96B368A-4504CFDA', 'White Cloud', 'Do shakera wrzuciæ kilka kawa³ków t³uczonego lodu. Wlaæ wszystkie sk³adniki i dobrze wstrz¹sn¹æ. Przecedziæ do p³askiej szampanki z niewielk¹ iloœci¹ t³uczonego lodu.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194040-0000175FFCA77C34-36E76746', 'White Lily No.1', 'Do shakera wrzuciæ kilka kostek. Wlaæ gin, rum i likiery, dobrze wstrz¹sn¹æ i przecedziæ do oziêbionego kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194049-000017620009C4B9-CE4FAD4A', 'White Russian', 'Do szklanicy barmañskiej wk³adamy lód i wlawamy wódkê oraz likier. Mieszamy, ¿eby sk³adniki dobrze siê sch³odzi³y. Przecedzamy do kieliszka koktajlowego. Po wypok³ej stronie ³y¿eczki delikatnie nalewamy œmietankê, tak ¿eby siê nie zmiesza³a z innymi sk³adnikami.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194056-00001763B3B12255-FBEEA26B', 'White Russian II', 'Do wysokiej szklanki wsypaæ kilka kostek lodu, wlaæ likier i wódkê, uzupe³niæ spritem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194105-00001765D87DCC29-E40EA2D8', 'Wielki Szlem', 'Kostki lodu wrzuciæ do shakera, wlaæ alkohol, wymieszaæ. Pozlaæ do szklanek, wrzuciæ jedn¹ lub dwie kostki lodu. Dope³niæ wod¹ sodow¹. Podawaæ udekorowane pomidorkami nawleczonymi na s³omkê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194113-0000176793092205-2CEDE19E', 'Williams Glory', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ sok z cytryny, wsypaæ cukier, dodaæ likier, wódkê i mocno wstrz¹cn¹æ. Przecedziæ do kieliszka koktajlowego i dope³niæ szampanem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194120-000017696A9BE163-53633ACF', 'Yellow Dream', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ Amaretto, sok z cytrynowy i bananowy. Wszystko dobrze wstrz¹sn¹æ. Przecedziæ do kieliszka koktajlowego i dope³niæ winem musuj¹cym. Przybraæ wiœni¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194128-0000176B48267503-0F236CA2', 'Zachêta', 'Brzeg kieliszka koktajlowego udekorowaæ cukrem. Na dno kieliszka wrzuciæ kostkê lodu, kawa³ki œwierzych owoców. Wlaæ likier wino i sok.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194136-0000176D22312A3F-3A5265C5', 'Z³ota £¹ka', 'Do wysokiej szampanki wlaæ likier i mocno sch³odzony szampan. Napój delikatnie zamieszaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194230-00001779B6C7DF28-F0F05128', 'Ananasowo-pomarañczowy', 'Wszystkie sk³adniki po³¹czyæ, dobrze wymieszaæ. Wstawiæ do lodówki, mocno sch³odziæ. Koktajl przelaæ do kieliszków, mo¿na wrzuciæ kostki lodu.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194238-0000177B8995D8A1-E4ECF3BF', 'Boris Special', 'Do shakera wrzuciæ kilka kostek lodu. Dodaæ ¿ó³tko, œmietankê, soki i Curacao, dobrze wstrz¹sn¹æ i przecedziæ do szklaneczki. Przybraæ ananasem i wiœni¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194246-0000177D696343CE-BDB2CFFC', 'Brandy Cobbler', 'W kieliszku do cobbleru lub w kieliszku do czerwonego wina rozpuœciæ cukier z odrobin¹ wody i nape³niæ kieliszek do wysokoœci 3/4 drobno t³uczonym lodem. Wlaæ brandy i zamieszaæ.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194254-0000177F4EF1D00F-6DB56404', 'Cytrusowy', 'Sok z pomarañczy, ananasa i cytryny zmieszaæ razem. Dodaæ sok z granatów. Sch³odziæ. Rozlaæ do pucharków, przystroiæ plasterkami grejpfruta.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194303-000017814315D693-2AEA3EC7', 'Cytrynowo-miêtowy', 'Sok z cytryny zmieszaæ z syropem miêtowym. Dodaæ tonik. Sch³odziæ. Przelaæ do kieliszków, przystroiæ plasterkami limony lub cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194310-00001782FCFEE4E0-3A8189B5', 'Florida', 'Do shakera wrzuciæ kilka kostek lodu. Dodaæ ¿ó³tko, syrop grenadynowy, soki cytrynowy, ananasowy i pomarañczowy, wstrz¹sn¹æ i przecedziæ do szklanicy do longdrinków. Na mieszade³ko nabiæ owoce i w³o¿yæ do szklanicy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194319-00001784F287EF6F-28939630', 'Golden Lemonade', 'Do shakera wrzuciæ kilka kostek lodu. Dodaæ ¿ó³tko, cukier i sok cytrynowy. Dobrze wymieszaæ wstrz¹saj¹æ. Przecedziæ do szklanicy do longdrinków, w której znajduj¹ siê 2 kostki lodu. Dope³niæ wod¹ sodow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194331-00001787E24E02E4-5B7E1590', 'Grejpfrutowy', 'Soki po³¹czyæ, zmieszaæ. Dope³niæ wod¹ mineraln¹. Kostki lodu wrzuciæ do kieliszków, przelaæ sok.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194340-00001789F565FFE4-9DAD1AA0', 'Jack and Coke', 'Do szklanki od whiskey wrzuciæ lód, nastêpnie dodaæ whiskey i Coca Colê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194348-0000178BBEC69797-265254DB', 'Lemon Flip', 'Brzegi szampanki zwil¿yæ sokiem cytrynowym, po czym zanurzyæ w czerwonym cukrze. Do shakera wrzuciæ kilka kostek lodu. Dodaæ ¿ó³tko, cukier i sok cytrynowy, dobrze wstrz¹sn¹æ i przecedziæ do szampanki.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194356-0000178DA74CF889-23F4DE47', 'Ogórkowy', 'Ogórki umyæ, obraæ, zetrzeæ na tarce o du¿ych oczkach. Sok wycisn¹æ przez p³ócienny woreczek. Przyprawiæ do smaku sokiem z cytryny i miodem. Do kieliszków wrzuciæ kilka kostek lodu, przelaæ sok, udekorowaæ plasterkiem cytryny.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194406-0000178FDD3441CA-C00BB6FA', 'Pomidorowy', 'Z pomidorów wycisn¹æ sok. Zmieszaæ z sokiem z cytryny. Dodaæ 1-2 krople tabasco, pieprz, sól, i sos worcester. Wymieszaæ. Do kieliszków w³o¿yæ kostki lodu, przelaæ sok. Posypaæ natk¹. Ga³¹zki selera naciowego bêd¹ spe³nia³y rolê mieszade³ek.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194416-000017923C84270B-1B2C8B45', 'Prairie Oyster II', 'Przyprawiæ sok pomidorowy, octem, Tabasco, sosem worcester i pieprzem, i wlaæ do p³askiej szampanki. Ostro¿nie wpuœciæ ¿ó³tko.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194423-00001793E0CE05D2-45050529', 'Rocky Mountain', 'Do shakera wrzuciæ kilka kostek lodu. Dodaæ wszystkie sk³adniki i dobrze wymieszaæ. Przecedziæ do szklanicy do longdrinków.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194430-0000179574ECDAEA-8BA8B644', 'Truskawkowy', 'Truskawki zmiksowaæ, wymieszaæ ze œmietank¹, przyprawiæ cukrem. Oziêbiæ. Do pucharków wrzuciæ kostki lodu, przelaæ koktajl. Udekorowaæ œwierzymi owocami.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194440-00001797CCCCB183-A0A0068B', 'Wieloowocowy', 'Wszystkie soki owocowe po³¹czyæ, wymieszaæ. Dodaæ do snaku toniku. Sch³odziæ. Wlaæ do wysokiego kieliszka. Ozdobiæ plasterkiem cytryny i czereœni¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194653-000017B6E06ADAB2-3BE10266', 'Americano', 'Campari i wermuth wlaæ do shakera, wymieszaæ. Do wysokiej szkalnki wrzuciæ kilka kostek lodu, przelaæ wymieszane alkohole i uzupe³niæ wod¹ sodow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194701-000017B8CFFB3F7A-80E0D45B', 'Appetizer Cocktail', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ angosturê, sok pomarañczowy, gin i wermut. Wstrz¹sn¹æ i przelaæ do oziêbionego kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194709-000017BAABE1077A-371C5307', 'Bacardi Martini', 'Do szklanicy barmañskiej wrzuciœ kilka kostek lodu. Wlaæ rum i wermut, zamieszaæ i przecedziæ do kieliszka koktajlowego. W³o¿yæ oliwkê.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194729-000017BF470BBD25-55DF8D17', 'Bermuda Highball', 'Do szklanki do longdrinków wrzuciæ kilka kostek lodu. Wlaæ gin, wermut i brandy, zamieszaæ, dope³niæ Ginger Ale.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194738-000017C16B4E445F-8C45FC7D', 'Blue Eyes', 'Wszystkie sk³adniki, oprócz toniku, wymieszaæ w szklance z kostkami lodu. Dope³niæ tonikiem, ponownie zamieszaæ. Udekorowaæ plasterkami pomarañczy lub banana, kiwi i czereœni¹ z syropu.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194758-000017C5DE0C6532-2030627D', 'Campari-Francy', 'Kostki lodu w³o¿yæ do szerokiej szklanki. Campari wymieszaæ z wermutem i sokiem cytrynowym, wlaæ do szklanki, dope³niæ sokiem pomarañczowym, zamieszaæ mieszade³kiem. Brzeg szklanki udekorowaæ cienkim plasterkiem pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194808-000017C8329EEAC7-E2F43921', 'Cardinal', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Wlaæ gin, Campari i wermut, zamieszaæ, przecedziæ do kieliszka koktajlowego. Skropiæ sokiem wyciœniêtym ze skórki cytrynowej i w³o¿yæ skórkê do drinka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194817-000017CA4AC65FFF-3D9EFE43', 'Cherry Inca', 'Jedn¹ kostkê lodu w³o¿yæ do kieliszka. 2-3 wrzuciæ do szklanki, wlaæ cherry, bia³y i czerwony wermut, dope³niæ wod¹ mineraln¹, dobrze zamieszaæ ³y¿k¹ barow¹, przelaæ przez sitko do kiliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194834-000017CE6C6FFD62-45AE293F', 'Dubarry Cocktail', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Dodaæ angosturê, wódkê any¿ow¹, wermut i gin, zamieszaæ i przecedziæ do oziêbionego kieliszka koktajlowego. Dodaæ 1/2 plastra pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194919-000017D8CA2F00AF-4A0BB540', 'Manhattan', 'W shakerze wymieszaæ alkohole, dodaæ pot³uczone kostki lodu, dobrze wstrz¹sn¹æ. Do kieliszka koktajlowego w³o¿yæ kostkê lodu, Koktajl przelaæ przez sitko, delikatnie odcedzaj¹c pokruszony lód. Udekorowaæ wisienk¹ koktajlow¹ i ewentualnie spiralk¹ ze skórki cytryny. Drink jest aperitifem, podajemy go przed posi³kiem.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130194939-000017DD957D660D-4D8EF4E9', 'Manhattan No.2', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Wlaæ gin, wermut i angosturê. Zamieszaæ i przecedziæ do ma³ego kieliszka koktajlowego. Przybraæ wiœni¹ koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130195007-000017E3F6331851-5A9A930C', 'Martini Sweet', 'Szklanicê barmañsk¹ nape³niæ lodem, dadaæ gin i wermutu. Zamieszaæ ³y¿k¹ barow¹ i przelaæ przez sito do kieliszka koktajlowego. Dekorujemy sprê¿ynk¹ ze skórki pomarañczy.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130195014-000017E5B70CAED6-CB6BB1CA', 'Negroni', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Wlaæ gin, Campari, wermut i zamieszaæ. Przecedziæ do kieliszka koktajlowego, skropiæ sokiem wyciœniêtym ze skórki cytrynowej, a skórkê w³o¿yæ do kieliszka.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130195034-000017EA40E2EEBB-C466CDC4', 'Perfect Martini', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu, wlaæ gin i wermut, zamieszaæ, przecedziæ do ma³ego kieliszka koktajlowego, skropiæ sokiem wyciœniêtym ze skórki cytrynowej.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130195054-000017EF07DE002B-939F7C3D', 'Rose', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Wlaæ wódkê, wermut i grenadynê, zamieszaæ i przecedziæ do ma³ego kieliszka koktajlowego. W³o¿yæ wiœniê koktajlow¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130195104-000017F149DAB7AD-A8FB272C', 'Salute', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ wermut, gin, Campari i syrop z czarnej porzeczki, dobrze wstrz¹sn¹æ. Do szklanki wrzuciæ 2-3 kostki lodu i przecedziæ drinka. Dope³niæ wod¹ sodow¹, przybraæ plastrem pomarañczy i limy oraz miêt¹.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130195112-000017F326748CBD-ACFD33B4', 'Star Cocktail', 'Do szklanicy barmañskiej wrzuciæ kilka kostek lodu. Wlaæ calvados, wermut i angosturê, zamieszaæ i przecedziæ do kieliszka koktajlowego. Skropliæ sokiem ze skórki cytrynowej.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130195154-000017FCFE310289-6A06FA20', 'Calvados Bowle', 'Jab³ko umyæ, wyci¹æ gniazda nasienne, pokroiæ, do naczynia do ponczu. Owoce zalaæ sokiem cytrynowym i calvadosem. Odstawiæ na 2 godz. w ch³odne miejsce. Wlaæ sok jab³kowy i szampan, wymieszaæ. Podawaæ w specjalnych szklankach do ponczu.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130195202-000017FEDB04681B-7551270E', 'Calvados Lime', 'Calvados i soki wlaæ do shakera, dodaæ kilka kostek pokruszonego lodu. Wymieszaæ, energicznie potrz¹saj¹c shakerem przez kilka sekund. Do kieliszka w³o¿yæ zwiniêty plasterek cytryny i pó³ plasterek czerwonego grejpfruta. Koktaj przelaæ przez sitko do kieliszka i dope³niæ piwem imbirowym.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130195210-000018008ED7484F-9AF7BEE7', 'Honeymoon Cocktail', 'Do shakera wrzuciæ kilka kostek lodu. Wlaæ wszystkie sk³adniki i wstrz¹sn¹æ. Przecedziæ do kieliszka koktajlowego.', null)
/
INSERT INTO DRINKS (DNK_ID, DNK_NAME, DNK_MAKE_UP, DNK_DESCRIPTION) VALUES ('20081130195225-000018043166C92E-6FA5B7DE', 'Jack Rose', 'Calvados, sok cytrynowy i grenadynê wlaæ do shakera, dodaæ pokruszony lód. Przez kilka sekund energicznie potrz¹saæ shakerem. Alkohol przelaæ przez sito do kiliszka koktajlowego. Podawaæ natychmiast po przygotowaniu.', null)
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130163901-00000D764AAFC514-5D721105', '20081130163841-00000D71B6F97B85-E30A787E', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130163901-00000D764AEFF56C-088EAC18', '20081130163841-00000D71B6F97B85-E30A787E', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130163901-00000D764B178C0B-7C15D398', '20081130163841-00000D71B6F97B85-E30A787E', 3, '5ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130163901-00000D764B3D75CD-8234D452', '20081130163841-00000D71B6F97B85-E30A787E', 4, '15ml triple sec')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130163901-00000D764B60E46F-8AB582F0', '20081130163841-00000D71B6F97B85-E30A787E', 5, '1 skórka z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130163950-00000D81C99A9EE5-D030F856', '20081130163926-00000D7C44426C69-4E785953', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130163950-00000D81C9BD15D0-FFE01E8D', '20081130163926-00000D7C44426C69-4E785953', 2, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130163950-00000D81C9DD1B6E-BD9200B8', '20081130163926-00000D7C44426C69-4E785953', 3, '2 porcje angostury')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130163950-00000D81CA2B7A6E-1D468176', '20081130163926-00000D7C44426C69-4E785953', 4, '1 limonka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164019-00000D889AF1E63A-12F35DFF', '20081130163957-00000D8377A3C1A6-E6B5D6F9', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164019-00000D889B14F2A6-41FBF2DE', '20081130163957-00000D8377A3C1A6-E6B5D6F9', 2, '30ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164019-00000D889B34AF27-C6C615DC', '20081130163957-00000D8377A3C1A6-E6B5D6F9', 3, '5ml creme de fraise')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164019-00000D889B579F34-B8918EA3', '20081130163957-00000D8377A3C1A6-E6B5D6F9', 4, '10ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164019-00000D889B77898B-FEB9B107', '20081130163957-00000D8377A3C1A6-E6B5D6F9', 5, '10ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164019-00000D889B997A3C-1737B191', '20081130163957-00000D8377A3C1A6-E6B5D6F9', 6, 'kilka truskawek')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164043-00000D8E1EB5DDC3-B9AC30D4', '20081130164022-00000D892BA695BA-9CA5A79F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164043-00000D8E1ED84109-43C6C9B7', '20081130164022-00000D892BA695BA-9CA5A79F', 2, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164043-00000D8E1EF7E014-FE82F067', '20081130164022-00000D892BA695BA-9CA5A79F', 3, '10ml Curacao niebieskie')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164043-00000D8E1F169250-EC416000', '20081130164022-00000D892BA695BA-9CA5A79F', 4, '15ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164043-00000D8E1F399947-30413E75', '20081130164022-00000D892BA695BA-9CA5A79F', 5, '10ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164104-00000D930EDFAF64-1786481C', '20081130164045-00000D8EA0BEF9DB-33A4A228', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164104-00000D930F06AE54-4A7E769A', '20081130164045-00000D8EA0BEF9DB-33A4A228', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164104-00000D930F276E8E-B3612E56', '20081130164045-00000D8EA0BEF9DB-33A4A228', 3, '15ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164104-00000D930F48C78F-0DBBF583', '20081130164045-00000D8EA0BEF9DB-33A4A228', 4, '25ml angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164104-00000D930F68946F-B24DF35D', '20081130164045-00000D8EA0BEF9DB-33A4A228', 5, '25ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164104-00000D930F877B0D-46DDAB09', '20081130164045-00000D8EA0BEF9DB-33A4A228', 6, '100ml imbirowego piwa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164125-00000D97D4AD82F5-01926BE4', '20081130164106-00000D937A7D98DF-D00F6230', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164125-00000D97D4CF9692-837DE7BF', '20081130164106-00000D937A7D98DF-D00F6230', 2, '40ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164125-00000D97D4F5E173-E27FFDC2', '20081130164106-00000D937A7D98DF-D00F6230', 3, '15ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164125-00000D97D515F99F-1431710A', '20081130164106-00000D937A7D98DF-D00F6230', 4, '15ml wermut rosso')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164125-00000D97D53606E1-37E259F3', '20081130164106-00000D937A7D98DF-D00F6230', 5, '15ml wermut wytrawny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164148-00000D9D53272EBF-3B3F4E86', '20081130164129-00000D98E0E34F8B-367A0FB1', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164148-00000D9D5348C3C5-ED7CC437', '20081130164129-00000D98E0E34F8B-367A0FB1', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164148-00000D9D5366FF0F-06CCA061', '20081130164129-00000D98E0E34F8B-367A0FB1', 3, '15ml Campari')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164148-00000D9D538466A3-7239BF2F', '20081130164129-00000D98E0E34F8B-367A0FB1', 4, '3ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164148-00000D9D53A20182-0ABD3AB4', '20081130164129-00000D98E0E34F8B-367A0FB1', 5, '100ml ale imbirowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164148-00000D9D53BFFB52-D3669F95', '20081130164129-00000D98E0E34F8B-367A0FB1', 6, '25ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164148-00000D9D53DDEE95-90DD7A02', '20081130164129-00000D98E0E34F8B-367A0FB1', 7, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164212-00000DA2BFBE9D1F-CAAF9739', '20081130164151-00000D9DDE36DD5D-9E8F227F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164212-00000DA2BFE4973E-5843197D', '20081130164151-00000D9DDE36DD5D-9E8F227F', 2, '30ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164212-00000DA2C003FD8A-7220C6F0', '20081130164151-00000D9DDE36DD5D-9E8F227F', 3, '50ml lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164212-00000DA2C021F0CD-1BD01B7F', '20081130164151-00000D9DDE36DD5D-9E8F227F', 4, '25ml sok z marakui')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164212-00000DA2C065144E-B3A13F96', '20081130164151-00000D9DDE36DD5D-9E8F227F', 5, '50ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164241-00000DA97D4EC314-FC9555E1', '20081130164213-00000DA30804B327-FE548BA8', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164241-00000DA97D71A837-8128BD8A', '20081130164213-00000DA30804B327-FE548BA8', 2, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164241-00000DA97D962AF1-20FC3ECD', '20081130164213-00000DA30804B327-FE548BA8', 3, '25ml creme de menthe - zielony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164241-00000DA97DB80D73-0482976B', '20081130164213-00000DA30804B327-FE548BA8', 4, '25ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164302-00000DAE6D65319C-0EC570E2', '20081130164243-00000DAA0997DD52-91631A2E', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164302-00000DAE6D87D9A3-17ADA39A', '20081130164243-00000DAA0997DD52-91631A2E', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164302-00000DAE6DA8E1E3-EB158BCD', '20081130164243-00000DAA0997DD52-91631A2E', 3, '10ml brandy cherry')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164302-00000DAE6DC9CCAC-4D847784', '20081130164243-00000DAA0997DD52-91631A2E', 4, '15ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164302-00000DAE6DECD3A4-D2837955', '20081130164243-00000DAA0997DD52-91631A2E', 5, '1 wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164324-00000DB390872CA1-2004ABF4', '20081130164303-00000DAEAF8C100E-45386D4D', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164324-00000DB390A9689F-9303C5E2', '20081130164303-00000DAEAF8C100E-45386D4D', 2, '30ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164324-00000DB390C71FDD-54416DAD', '20081130164303-00000DAEAF8C100E-45386D4D', 3, '10ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164324-00000DB390E78AF9-F2B36E66', '20081130164303-00000DAEAF8C100E-45386D4D', 4, '5ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164324-00000DB39105E2A1-65512BE0', '20081130164303-00000DAEAF8C100E-45386D4D', 5, '5ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164341-00000DB774A2C84C-DFE70F13', '20081130164325-00000DB3ECABD835-FBC00564', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164341-00000DB774C4C615-968B1E94', '20081130164325-00000DB3ECABD835-FBC00564', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164341-00000DB774ECD6EE-045CBAFA', '20081130164325-00000DB3ECABD835-FBC00564', 3, '1 bia³ko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164341-00000DB7750ECF43-81B5A2BF', '20081130164325-00000DB3ECABD835-FBC00564', 4, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164341-00000DB7752CAB9B-DF424128', '20081130164325-00000DB3ECABD835-FBC00564', 5, 'sok z cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164400-00000DBC042BA8C5-41C3BFC3', '20081130164342-00000DB7DBFD8B39-7584D27B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164400-00000DBC044D2910-3D9A57AB', '20081130164342-00000DB7DBFD8B39-7584D27B', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164400-00000DBC046ABD62-5B2D0DD7', '20081130164342-00000DB7DBFD8B39-7584D27B', 3, '5ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164400-00000DBC04882B83-2348F75C', '20081130164342-00000DB7DBFD8B39-7584D27B', 4, '3ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164400-00000DBC04A6B9BC-E24D79FC', '20081130164342-00000DB7DBFD8B39-7584D27B', 5, '15ml porto')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164427-00000DC248C888FF-F3FB5166', '20081130164401-00000DBC41E541D1-21F94C3A', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164427-00000DC248E84469-49721699', '20081130164401-00000DBC41E541D1-21F94C3A', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164427-00000DC2490C8A07-08015225', '20081130164401-00000DBC41E541D1-21F94C3A', 3, '20ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164427-00000DC2492A6ABD-F9885D08', '20081130164401-00000DBC41E541D1-21F94C3A', 4, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164427-00000DC24947D6AF-2A83F699', '20081130164401-00000DBC41E541D1-21F94C3A', 5, '125ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164451-00000DC7CE47E09D-68917775', '20081130164431-00000DC345F51D29-5FEB4363', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164451-00000DC7CE695944-3AED2161', '20081130164431-00000DC345F51D29-5FEB4363', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164451-00000DC7CE87313F-26FA971C', '20081130164431-00000DC345F51D29-5FEB4363', 3, '5ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164451-00000DC7CEA497BD-A9DF50CA', '20081130164431-00000DC345F51D29-5FEB4363', 4, '3ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164451-00000DC7CEC1AFA7-9B009B17', '20081130164431-00000DC345F51D29-5FEB4363', 5, '15ml porto')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164516-00000DCD9194B2F6-73859D6F', '20081130164452-00000DC8228A7A6E-06C188D0', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164516-00000DCD91DD3F48-2CF3CC9B', '20081130164452-00000DC8228A7A6E-06C188D0', 2, '15ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164516-00000DCD91FDF03C-8ABB01E1', '20081130164452-00000DC8228A7A6E-06C188D0', 3, '100ml ale imbirowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164543-00000DD3FB02FD26-70787A3F', '20081130164528-00000DD085C646F0-D109857A', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164543-00000DD3FB25B12D-70A6F09F', '20081130164528-00000DD085C646F0-D109857A', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164543-00000DD3FB437E3F-72E33812', '20081130164528-00000DD085C646F0-D109857A', 3, '10ml sok z cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164543-00000DD3FB609629-A9FCEC27', '20081130164528-00000DD085C646F0-D109857A', 4, '15ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164601-00000DD81F53A11C-3958962D', '20081130164544-00000DD44AD37127-8DC1C263', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164601-00000DD81F747B87-F57FF39A', '20081130164544-00000DD44AD37127-8DC1C263', 2, '40ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164601-00000DD81F92526B-CFB7FF86', '20081130164544-00000DD44AD37127-8DC1C263', 3, '40ml wermut wytrawny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164601-00000DD81FB28A3C-E062F8B2', '20081130164544-00000DD44AD37127-8DC1C263', 4, '1 oliwka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164619-00000DDC6AAF156C-CDC7C624', '20081130164602-00000DD87D2AFEF1-2D29FB3B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164619-00000DDC6ACFEC91-00026F67', '20081130164602-00000DD87D2AFEF1-2D29FB3B', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164619-00000DDC6AED959F-24EECCCA', '20081130164602-00000DD87D2AFEF1-2D29FB3B', 3, '15ml triple sec')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164643-00000DE1E288264D-D7033975', '20081130164621-00000DDCC1A4BD61-B03A52CC', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164643-00000DE1E2A95179-91C55D56', '20081130164621-00000DDCC1A4BD61-B03A52CC', 2, '30ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164643-00000DE1E2C684AC-20D10CCB', '20081130164621-00000DDCC1A4BD61-B03A52CC', 3, '15ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164643-00000DE1E2E4DE84-F416C3AB', '20081130164621-00000DDCC1A4BD61-B03A52CC', 4, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164643-00000DE1E302B9C5-45C0B95F', '20081130164621-00000DDCC1A4BD61-B03A52CC', 5, '100ml gorzka gazowana lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164709-00000DE7EA005E75-3F98068E', '20081130164654-00000DE471FD8595-5CC544BF', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164709-00000DE7EA245469-BEF5C699', '20081130164654-00000DE471FD8595-5CC544BF', 2, '25ml d¿in Plymuth')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164709-00000DE7EA43D5FC-F80AC2E5', '20081130164654-00000DE471FD8595-5CC544BF', 3, '5ml niebieskie Curacao')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164709-00000DE7EA637CAA-FCD82254', '20081130164654-00000DE471FD8595-5CC544BF', 4, '25ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164709-00000DE7EA80E0F9-A1AA4F14', '20081130164654-00000DE471FD8595-5CC544BF', 5, '10ml Galliano')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164709-00000DE7EA9DB1F5-9BE1FDF1', '20081130164654-00000DE471FD8595-5CC544BF', 6, '50ml gazowana lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164737-00000DEE74870931-B66BDAEF', '20081130164723-00000DEB468C2588-EF50677D', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164737-00000DEE74A9558D-F3109F39', '20081130164723-00000DEB468C2588-EF50677D', 2, '30ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164737-00000DEE74C754D1-7FE1BC06', '20081130164723-00000DEB468C2588-EF50677D', 3, '5ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164737-00000DEE74E48804-087EAD66', '20081130164723-00000DEB468C2588-EF50677D', 4, '5ml miód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164758-00000DF35CBFEAB6-2BE72A6D', '20081130164744-00000DF009E2A6EC-19E2B84B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164758-00000DF35CE10126-FBAF9D4D', '20081130164744-00000DF009E2A6EC-19E2B84B', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164758-00000DF35CFF9C77-4191DBA1', '20081130164744-00000DF009E2A6EC-19E2B84B', 3, '10ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130164758-00000DF35D1D2926-3AF63E6B', '20081130164744-00000DF009E2A6EC-19E2B84B', 4, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170206-00000EB8C2A045D6-6D63F006', '20081130170148-00000EB48E3C4977-4728B367', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170206-00000EB8C2D8E2FE-5DF08D52', '20081130170148-00000EB48E3C4977-4728B367', 2, '40ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170206-00000EB8C2F9BA23-7753EB36', '20081130170148-00000EB48E3C4977-4728B367', 3, '25ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170206-00000EB8C318485C-A1043D15', '20081130170148-00000EB48E3C4977-4728B367', 4, '25ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170206-00000EB8C3386C89-7D56E655', '20081130170148-00000EB48E3C4977-4728B367', 5, '25ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170228-00000EBDEC062901-86B3D5D8', '20081130170213-00000EBA63216D83-42207B53', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170228-00000EBDEC26E198-083D6D9F', '20081130170213-00000EBA63216D83-42207B53', 2, '65ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170228-00000EBDEC445874-20B18129', '20081130170213-00000EBA63216D83-42207B53', 3, '5ml wermut wytrawny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170228-00000EBDEC6256A1-1B3B29CD', '20081130170213-00000EBA63216D83-42207B53', 4, '2 cebulki koktajlowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170248-00000EC291653434-EE9F5D17', '20081130170230-00000EBE60909B89-F2AF7797', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170248-00000EC29185851E-BC2CA7D4', '20081130170230-00000EBE60909B89-F2AF7797', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170248-00000EC291A32689-98199037', '20081130170230-00000EBE60909B89-F2AF7797', 3, '15ml sok z limonki')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170248-00000EC291C06277-687FE960', '20081130170230-00000EBE60909B89-F2AF7797', 4, '25ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170307-00000EC6EFF3926E-E8B21CEF', '20081130170249-00000EC2D8E27999-482CB46F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170307-00000EC6F012A16C-A452578A', '20081130170249-00000EC2D8E27999-482CB46F', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170307-00000EC6F033BA0B-A541CC87', '20081130170249-00000EC2D8E27999-482CB46F', 3, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170307-00000EC6F0504301-4F09AA96', '20081130170249-00000EC2D8E27999-482CB46F', 4, 'owoce')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170307-00000EC6F0955820-C667E67B', '20081130170249-00000EC2D8E27999-482CB46F', 5, '100ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170324-00000ECAF528013B-62F3EFD9', '20081130170308-00000EC75CBE8ADB-54877FA6', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170324-00000ECAF56299D4-D496763C', '20081130170308-00000EC75CBE8ADB-54877FA6', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170324-00000ECAF58801B9-3E844314', '20081130170308-00000EC75CBE8ADB-54877FA6', 3, '25ml pomarañczówki')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170344-00000ECFBE45DAC6-742F9240', '20081130170325-00000ECB35DF7834-457ED762', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170344-00000ECFBE64C393-59D3D2F7', '20081130170325-00000ECB35DF7834-457ED762', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170344-00000ECFBE802187-2FB39D74', '20081130170325-00000ECB35DF7834-457ED762', 3, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170344-00000ECFBEC4C63F-38D1DC98', '20081130170325-00000ECB35DF7834-457ED762', 4, '3ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170344-00000ECFBEE18051-D97F4EFA', '20081130170325-00000ECB35DF7834-457ED762', 5, '3ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170344-00000ECFBEFF0445-FA2C2ECE', '20081130170325-00000ECB35DF7834-457ED762', 6, '1 wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170344-00000ECFBF1D2100-39DBD7E1', '20081130170325-00000ECB35DF7834-457ED762', 7, '1 plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170344-00000ECFBF37D5CE-0C60A167', '20081130170325-00000ECB35DF7834-457ED762', 8, '1 ga³¹zka miêty')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170344-00000ECFBF525EF6-9AFB5BDB', '20081130170325-00000ECB35DF7834-457ED762', 9, 'pó³ ³y¿eczki cukru pudru')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170412-00000ED6437FB4D3-6F7F2F27', '20081130170400-00000ED35207A32B-F56FEB50', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170412-00000ED6439E7656-DEF0BD27', '20081130170400-00000ED35207A32B-F56FEB50', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170412-00000ED643B9549D-CD95CC15', '20081130170400-00000ED35207A32B-F56FEB50', 3, '3ml syropu cukrowego')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170412-00000ED643D4F639-0FC88ABC', '20081130170400-00000ED35207A32B-F56FEB50', 4, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170412-00000ED643EFAC1F-5296FFB3', '20081130170400-00000ED35207A32B-F56FEB50', 5, '125ml woda gazowana (najlepiej z syfonu)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170430-00000EDA6085DEC5-6FEE72ED', '20081130170414-00000ED6A43C5B3C-FCFC5893', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170430-00000EDA60A77712-8300D41E', '20081130170414-00000ED6A43C5B3C-FCFC5893', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170430-00000EDA60C31D0C-941F9AFE', '20081130170414-00000ED6A43C5B3C-FCFC5893', 3, '100ml wody sodowej')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170430-00000EDA60DE735C-3C889280', '20081130170414-00000ED6A43C5B3C-FCFC5893', 4, 'sok z limonki')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170449-00000EDECA56AFF0-D155A699', '20081130170431-00000EDAB616B085-98287633', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170449-00000EDECA7433E4-C7C5446A', '20081130170431-00000EDAB616B085-98287633', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170449-00000EDECA95F491-C43734CD', '20081130170431-00000EDAB616B085-98287633', 3, '50ml woda')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170449-00000EDECAB00F81-9686996B', '20081130170431-00000EDAB616B085-98287633', 4, '15ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170449-00000EDECACD7945-B24897A7', '20081130170431-00000EDAB616B085-98287633', 5, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170508-00000EE31E4E2CAA-B16E4147', '20081130170450-00000EDF085B9AA2-1114C1CB', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170508-00000EE31E7343CE-0AAC4647', '20081130170450-00000EDF085B9AA2-1114C1CB', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170508-00000EE31E8E7ED6-0A011598', '20081130170450-00000EDF085B9AA2-1114C1CB', 3, '15ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170508-00000EE31EB332AC-F592F923', '20081130170450-00000EDF085B9AA2-1114C1CB', 4, '4 ga³¹zki miêty')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170525-00000EE711A1C971-19389219', '20081130170509-00000EE3634721B2-51D256D0', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170525-00000EE711C072F2-32530CEB', '20081130170509-00000EE3634721B2-51D256D0', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170525-00000EE711DBB258-8A3F3A42', '20081130170509-00000EE3634721B2-51D256D0', 3, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170525-00000EE711F82235-3C0CC528', '20081130170509-00000EE3634721B2-51D256D0', 4, 'sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170542-00000EEB32A24B0E-D97629FB', '20081130170526-00000EE75F8B35AB-CDE419DB', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170542-00000EEB32C04C81-E97AF179', '20081130170526-00000EE75F8B35AB-CDE419DB', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170542-00000EEB32DAB603-AC599F48', '20081130170526-00000EE75F8B35AB-CDE419DB', 3, '40ml sok z limonki')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170542-00000EEB32F4A31E-F6D77515', '20081130170526-00000EE75F8B35AB-CDE419DB', 4, '1 ³y¿eczka do herbaty cukru pudru')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170542-00000EEB330EC49A-0D677B1E', '20081130170526-00000EE75F8B35AB-CDE419DB', 5, '75ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170559-00000EEF02E32981-56D88914', '20081130170543-00000EEB76D1B5EE-75FE9442', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170559-00000EEF030111DB-17C77324', '20081130170543-00000EEB76D1B5EE-75FE9442', 2, '30ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170559-00000EEF031CE493-7C799D8F', '20081130170543-00000EEB76D1B5EE-75FE9442', 3, '5ml likier maraschino')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170559-00000EEF033731B6-40AA9A65', '20081130170543-00000EEB76D1B5EE-75FE9442', 4, '25ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170559-00000EEF03519F95-5AA42617', '20081130170543-00000EEB76D1B5EE-75FE9442', 5, '1 wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170614-00000EF294EC689A-CF4B5CDA', '20081130170600-00000EEF43F9853E-45509C06', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170614-00000EF2950A6C3C-58614F2E', '20081130170600-00000EEF43F9853E-45509C06', 2, '40ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170614-00000EF2952529C5-21E6F04C', '20081130170600-00000EEF43F9853E-45509C06', 3, 'pó³ ³y¿eczki grenadyny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170614-00000EF29542EE1C-DC2BD4AC', '20081130170600-00000EEF43F9853E-45509C06', 4, '25ml soku ananasowego')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170614-00000EF2955EBA48-9C222EFC', '20081130170600-00000EEF43F9853E-45509C06', 5, '25ml triple sec')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170614-00000EF2957C2BAF-F77F1207', '20081130170600-00000EEF43F9853E-45509C06', 6, 'plasterek ananasa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170630-00000EF66B21C707-69AE5FC6', '20081130170615-00000EF2E5CDD75B-786BDC1E', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170630-00000EF66B40A1A4-992395F6', '20081130170615-00000EF2E5CDD75B-786BDC1E', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170630-00000EF66B5C396E-548B102A', '20081130170615-00000EF2E5CDD75B-786BDC1E', 3, '5ml angostury')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170630-00000EF66B76A0C1-6D3CBB75', '20081130170615-00000EF2E5CDD75B-786BDC1E', 4, '5ml Curacao')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170630-00000EF66B9022EA-575F4A04', '20081130170615-00000EF2E5CDD75B-786BDC1E', 5, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170630-00000EF66BAA4695-21E70ABC', '20081130170615-00000EF2E5CDD75B-786BDC1E', 6, '15ml amaretto')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170648-00000EFA97627C7B-759E71AC', '20081130170632-00000EF6B4F2A4AA-AF3B5F5F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170648-00000EFA97800813-CEAC2B71', '20081130170632-00000EF6B4F2A4AA-AF3B5F5F', 2, '40ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170648-00000EFA979A9369-96AA6490', '20081130170632-00000EF6B4F2A4AA-AF3B5F5F', 3, '5ml soku pomarañczowego')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170648-00000EFA97B51FD7-91D084FF', '20081130170632-00000EF6B4F2A4AA-AF3B5F5F', 4, '5ml soku ananasowego')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170648-00000EFA97CF7040-63E50FAC', '20081130170632-00000EF6B4F2A4AA-AF3B5F5F', 5, '5ml soku cytrynowego')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170648-00000EFA97E9AF33-AA244994', '20081130170632-00000EF6B4F2A4AA-AF3B5F5F', 6, 'pó³ ³y¿eczki do herbaty cukru pudru')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170710-00000EFFAC6612F5-8D8351E5', '20081130170650-00000EFAFD28D8CE-2F0DB58E', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170710-00000EFFACBC3758-933DDC57', '20081130170650-00000EFAFD28D8CE-2F0DB58E', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170710-00000EFFACD81FE3-77AC6D48', '20081130170650-00000EFAFD28D8CE-2F0DB58E', 3, 'piwo imbirowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170710-00000EFFACF2727B-C9455530', '20081130170650-00000EFAFD28D8CE-2F0DB58E', 4, '1 ca³a cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170728-00000F03C852DFA1-D6A15584', '20081130170713-00000F003F4E6D16-F0DF9164', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170728-00000F03C871D69D-4D1F33BC', '20081130170713-00000F003F4E6D16-F0DF9164', 2, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170728-00000F03C88EE058-7785AE37', '20081130170713-00000F003F4E6D16-F0DF9164', 3, '15ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170728-00000F03C8AB85AE-C0FB0EF8', '20081130170713-00000F003F4E6D16-F0DF9164', 4, '15ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170728-00000F03C8C597E3-BF9C59FD', '20081130170713-00000F003F4E6D16-F0DF9164', 5, '50ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170728-00000F03C8DFF107-DFAC751F', '20081130170713-00000F003F4E6D16-F0DF9164', 6, '50ml sok z marakui')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170745-00000F07A9C98087-552FEEA3', '20081130170729-00000F040F67330D-CFB2ED2B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170745-00000F07A9E7D830-BB5D891C', '20081130170729-00000F040F67330D-CFB2ED2B', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170745-00000F07AA027DB7-124D404F', '20081130170729-00000F040F67330D-CFB2ED2B', 3, '10ml czerwony wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170745-00000F07AA1C8503-62C36D28', '20081130170729-00000F040F67330D-CFB2ED2B', 4, '10ml Grand Marnier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170745-00000F07AA36BD6A-281C64DF', '20081130170729-00000F040F67330D-CFB2ED2B', 5, 'sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170806-00000F0CC44D986F-FACC5366', '20081130170756-00000F0A520F9ABA-22A70EB6', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170806-00000F0CC46B69DE-4ABFCDC6', '20081130170756-00000F0A520F9ABA-22A70EB6', 2, '30ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170806-00000F0CC485B2A3-89E66332', '20081130170756-00000F0A520F9ABA-22A70EB6', 3, '1ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170806-00000F0CC49F9FBE-CFEB6DC9', '20081130170756-00000F0A520F9ABA-22A70EB6', 4, '5ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170806-00000F0CC4BA2D43-6FE56830', '20081130170756-00000F0A520F9ABA-22A70EB6', 5, '30ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170806-00000F0CC4D44833-F37A65E1', '20081130170756-00000F0A520F9ABA-22A70EB6', 6, '50ml sok z mandarynki')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170827-00000F1194B4BA70-2301576A', '20081130170808-00000F0D06EDC754-D179C266', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170827-00000F1194D29D55-598B48AC', '20081130170808-00000F0D06EDC754-D179C266', 2, '60ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170827-00000F1194ED4EDD-95AB44B1', '20081130170808-00000F0D06EDC754-D179C266', 3, '25ml wermut wytrawny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170827-00000F119509570E-4D414B1E', '20081130170808-00000F0D06EDC754-D179C266', 4, '1 zielona oliwka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170845-00000F15AC6F3085-F8BC08D8', '20081130170828-00000F11DCA5B773-490F27C0', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170845-00000F15AC8DAF77-382ABE51', '20081130170828-00000F11DCA5B773-490F27C0', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170845-00000F15ACA8572D-C5D6BAB6', '20081130170828-00000F11DCA5B773-490F27C0', 3, '50ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170845-00000F15ACC4A535-8A298C7A', '20081130170828-00000F11DCA5B773-490F27C0', 4, '25ml triple sec')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170902-00000F19B884FDE4-3153ADA4', '20081130170846-00000F15EA49E22F-70BDC140', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170902-00000F19B8A34DE9-F138E500', '20081130170846-00000F15EA49E22F-70BDC140', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170902-00000F19B8BFF9CB-22EA3607', '20081130170846-00000F15EA49E22F-70BDC140', 3, '10ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170902-00000F19B8DA9698-5B08A902', '20081130170846-00000F15EA49E22F-70BDC140', 4, '25ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170920-00000F1DE9272D33-116751C4', '20081130170904-00000F1A0F46816F-2866C6E3', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170920-00000F1DE9469F7F-B3D11117', '20081130170904-00000F1A0F46816F-2866C6E3', 2, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170920-00000F1DE9649094-A744DC42', '20081130170904-00000F1A0F46816F-2866C6E3', 3, '10ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170920-00000F1DE97F6EDA-72060CEF', '20081130170904-00000F1A0F46816F-2866C6E3', 4, '10ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170920-00000F1DE99A9755-FA0DFDAA', '20081130170904-00000F1A0F46816F-2866C6E3', 5, '1 bia³ko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170920-00000F1DE9B7144A-5222275C', '20081130170904-00000F1A0F46816F-2866C6E3', 6, '5ml creme de fraise')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170940-00000F2274B4F820-D02D1809', '20081130170922-00000F1E4115DAF7-B0A35D64', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170940-00000F2274D2BC76-F6A0D3EE', '20081130170922-00000F1E4115DAF7-B0A35D64', 2, '40ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170940-00000F2274ED4E59-7734E229', '20081130170922-00000F1E4115DAF7-B0A35D64', 3, '40ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170940-00000F2275075B19-7F7C0E7D', '20081130170922-00000F1E4115DAF7-B0A35D64', 4, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170940-00000F2275214EC0-ED98CB30', '20081130170922-00000F1E4115DAF7-B0A35D64', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170940-00000F22753ACEBA-C05BB072', '20081130170922-00000F1E4115DAF7-B0A35D64', 6, 'pó³ ³y¿eczki od herbaty cukru pudru')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170959-00000F26EA6E441B-6EDBEE78', '20081130170942-00000F22FF23662E-51101D0B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170959-00000F26EA8C4131-293C1F27', '20081130170942-00000F22FF23662E-51101D0B', 2, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170959-00000F26EAA67DF5-AFB6AC88', '20081130170942-00000F22FF23662E-51101D0B', 3, '25ml creme de cassis')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130170959-00000F26EAC0C8E9-3A8B8A0F', '20081130170942-00000F22FF23662E-51101D0B', 4, '25ml Noilly Prat')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171017-00000F2B2A8B07FA-81948437', '20081130171001-00000F2764EB904A-D346FE2C', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171017-00000F2B2AA6FFCD-8251F575', '20081130171001-00000F2764EB904A-D346FE2C', 2, '30ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171017-00000F2B2AC131A8-CBD0470F', '20081130171001-00000F2764EB904A-D346FE2C', 3, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171017-00000F2B2ADB3267-62207C32', '20081130171001-00000F2764EB904A-D346FE2C', 4, '10ml cherry brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171017-00000F2B2AF51A0D-B098DBC4', '20081130171001-00000F2764EB904A-D346FE2C', 5, '5ml maraschino')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171017-00000F2B2B10911A-D7A25DF0', '20081130171001-00000F2764EB904A-D346FE2C', 6, '1 wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171033-00000F2EEB90E2E1-40DC9FEE', '20081130171018-00000F2B7EEEDB27-F37490C5', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171033-00000F2EEBAEDFF7-F32D8640', '20081130171018-00000F2B7EEEDB27-F37490C5', 2, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171033-00000F2EEBC99C69-1D977395', '20081130171018-00000F2B7EEEDB27-F37490C5', 3, '25ml bia³ka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171033-00000F2EEBE4487D-1E7128C2', '20081130171018-00000F2B7EEEDB27-F37490C5', 4, '10ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171033-00000F2EEBFE36AF-517A428D', '20081130171018-00000F2B7EEEDB27-F37490C5', 5, '10ml brzoskwiniowa brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171051-00000F332DA8DFE0-3913E848', '20081130171035-00000F2F59FE831D-304C3B5A', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171051-00000F332DC6C097-82433A02', '20081130171035-00000F2F59FE831D-304C3B5A', 2, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171051-00000F332DE28BAB-3ABBF1BB', '20081130171035-00000F2F59FE831D-304C3B5A', 3, '10ml œmietana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171051-00000F332DFC6EF3-C8B4F149', '20081130171035-00000F2F59FE831D-304C3B5A', 4, '25ml bia³ka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171051-00000F332E189D56-5784FD79', '20081130171035-00000F2F59FE831D-304C3B5A', 5, '5ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171051-00000F332E328FE6-F6BE378B', '20081130171035-00000F2F59FE831D-304C3B5A', 6, '10ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171051-00000F332E5299E2-08587735', '20081130171035-00000F2F59FE831D-304C3B5A', 7, '1 wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171106-00000F3694223965-391838DE', '20081130171052-00000F3365AAA2D0-7CC150B6', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171106-00000F36943F177A-BF770E65', '20081130171052-00000F3365AAA2D0-7CC150B6', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171106-00000F3694594EC9-E0C9DDF2', '20081130171052-00000F3365AAA2D0-7CC150B6', 3, '50ml sok grapefruitowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171106-00000F3694753CC9-4C6FAE7D', '20081130171052-00000F3365AAA2D0-7CC150B6', 4, '10ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171106-00000F369491E794-42DE2680', '20081130171052-00000F3365AAA2D0-7CC150B6', 5, '75ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171125-00000F3AE2AFF4CD-BFB42994', '20081130171107-00000F36E21AAC59-738211EC', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171125-00000F3AE2CD468E-A89CF6E2', '20081130171107-00000F36E21AAC59-738211EC', 2, '30ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171125-00000F3AE2E8364A-DE13C183', '20081130171107-00000F36E21AAC59-738211EC', 3, '5ml pomarañczówka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171125-00000F3AE30377DF-4D835ED2', '20081130171107-00000F36E21AAC59-738211EC', 4, '15ml ciemne piwo')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171125-00000F3AE3210D49-C28B91D9', '20081130171107-00000F36E21AAC59-738211EC', 5, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171149-00000F40A0075EBB-4ABC37A5', '20081130171127-00000F3B643A0280-3FA5A39E', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171149-00000F40A02538E5-472B8CB4', '20081130171127-00000F3B643A0280-3FA5A39E', 2, '30ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171149-00000F40A03DF35A-C5AB01FE', '20081130171127-00000F3B643A0280-3FA5A39E', 3, '10ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171149-00000F40A055F21C-935464D9', '20081130171127-00000F3B643A0280-3FA5A39E', 4, '15ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171149-00000F40A06EE77F-D2068CAF', '20081130171127-00000F3B643A0280-3FA5A39E', 5, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171149-00000F40A092E517-F92CA3EF', '20081130171127-00000F3B643A0280-3FA5A39E', 6, '1 wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171149-00000F40A0B07DC7-620AAFEE', '20081130171127-00000F3B643A0280-3FA5A39E', 7, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171204-00000F4429762A47-2A0DB5F3', '20081130171150-00000F40DF983120-577D7AA1', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171204-00000F442994E99C-F4335F18', '20081130171150-00000F40DF983120-577D7AA1', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171204-00000F4429AFADB1-35CA17EC', '20081130171150-00000F40DF983120-577D7AA1', 3, '10ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171204-00000F4429C8FDA7-AA895A15', '20081130171150-00000F40DF983120-577D7AA1', 4, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171222-00000F4851BDE774-8D3CF62A', '20081130171206-00000F446D5C986E-CD5D3A49', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171222-00000F4851DB607E-40305F37', '20081130171206-00000F446D5C986E-CD5D3A49', 2, '50ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171222-00000F4851F40CC3-63C02925', '20081130171206-00000F446D5C986E-CD5D3A49', 3, '20ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171222-00000F48520C0B86-DCD546FE', '20081130171206-00000F446D5C986E-CD5D3A49', 4, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171241-00000F4CB8E7302E-E11C3196', '20081130171223-00000F489BCF0059-62F26D67', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171241-00000F4CB904A3C4-33A3DEFD', '20081130171223-00000F489BCF0059-62F26D67', 2, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171241-00000F4CB91F7FDC-112C1E3A', '20081130171223-00000F489BCF0059-62F26D67', 3, '25ml Mandarine Napoleon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171241-00000F4CB938536A-14D67C19', '20081130171223-00000F489BCF0059-62F26D67', 4, '25ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171241-00000F4CB950B463-75069701', '20081130171223-00000F489BCF0059-62F26D67', 5, 'skórka pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171324-00000F56A6C793A6-58BC065B', '20081130171301-00000F5161570AE9-72795699', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171324-00000F56A6E1F23F-EE2B199B', '20081130171301-00000F5161570AE9-72795699', 2, '15ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171324-00000F56A6FA4FF2-0B49E4AC', '20081130171301-00000F5161570AE9-72795699', 3, '5ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171324-00000F56A71558C7-389A944A', '20081130171301-00000F5161570AE9-72795699', 4, '5ml creme de cassis')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171324-00000F56A7388936-D2C265B5', '20081130171301-00000F5161570AE9-72795699', 5, '100ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171324-00000F56A7579C92-6085EF80', '20081130171301-00000F5161570AE9-72795699', 6, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171340-00000F5A6EC8D23C-7D8D6FED', '20081130171325-00000F56FE074B55-EDAA10F4', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171340-00000F5A6EE62973-474658A7', '20081130171325-00000F56FE074B55-EDAA10F4', 2, '40ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171340-00000F5A6EFEAC40-90414583', '20081130171325-00000F56FE074B55-EDAA10F4', 3, '40ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130171340-00000F5A6F176C2A-88E12825', '20081130171325-00000F56FE074B55-EDAA10F4', 4, '40ml triple sec')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172125-00000FC695556A7B-C109B200', '20081130172054-00000FBF8F2C7BDA-2573AE62', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172125-00000FC695717EAD-EAD2E5DC', '20081130172054-00000FBF8F2C7BDA-2573AE62', 2, '25ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172125-00000FC69589C9D3-3A5AFB61', '20081130172054-00000FBF8F2C7BDA-2573AE62', 3, '10ml creme de menthe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172125-00000FC695A23387-CA00531D', '20081130172054-00000FBF8F2C7BDA-2573AE62', 4, '25ml Pernod')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172125-00000FC695B9B928-8C1E0FF2', '20081130172054-00000FBF8F2C7BDA-2573AE62', 5, '100ml lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172125-00000FC695D1B15E-7E7D96CC', '20081130172054-00000FBF8F2C7BDA-2573AE62', 6, 'miêta')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172125-00000FC695E91EFD-191D98B9', '20081130172054-00000FBF8F2C7BDA-2573AE62', 7, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172150-00000FCC6B29F0FA-E5745ACB', '20081130172127-00000FC71FAEBA63-835667FE', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172150-00000FCC6B46F99E-097ABE58', '20081130172127-00000FC71FAEBA63-835667FE', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172150-00000FCC6B5F0004-B0E46E80', '20081130172127-00000FC71FAEBA63-835667FE', 3, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172150-00000FCC6B769832-53106AB6', '20081130172127-00000FC71FAEBA63-835667FE', 4, '15ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172150-00000FCC6B8E6C65-463F3A29', '20081130172127-00000FC71FAEBA63-835667FE', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172150-00000FCC6BA606C1-493BA785', '20081130172127-00000FC71FAEBA63-835667FE', 6, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172208-00000FD0C710B666-730DFBA3', '20081130172151-00000FCCD5259EB0-46A78A42', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172208-00000FD0C72E0EB3-2BEA482B', '20081130172151-00000FCCD5259EB0-46A78A42', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172208-00000FD0C7467521-42C7228D', '20081130172151-00000FCCD5259EB0-46A78A42', 3, '25ml Bailey''s')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172230-00000FD5EB852C9F-F6F7486E', '20081130172211-00000FD1578A2884-7174831A', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172230-00000FD5EBA201F8-85A971E4', '20081130172211-00000FD1578A2884-7174831A', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172230-00000FD5EBBE1FFD-F3E528D1', '20081130172211-00000FD1578A2884-7174831A', 3, '10ml œmietana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172230-00000FD5EBD7C0B4-503348F9', '20081130172211-00000FD1578A2884-7174831A', 4, '25ml creme de cacao')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172230-00000FD5EBEFB8EA-C633D128', '20081130172211-00000FD1578A2884-7174831A', 5, 'czekolada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172253-00000FDB1892363C-A17E47AD', '20081130172233-00000FD6A0D537AF-A20A8152', 1, '25ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172253-00000FDB18AD24E1-2022E02C', '20081130172233-00000FD6A0D537AF-A20A8152', 2, '300 - 500ml piwo Guinness')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172331-00000FE41A8536F8-31583501', '20081130172316-00000FE098E0CF2D-E3C8FB24', 1, 'Lód 30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172331-00000FE41AA34E3E-C283EEA5', '20081130172316-00000FE098E0CF2D-E3C8FB24', 2, '15ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172331-00000FE41ABB9735-DDDCFD66', '20081130172316-00000FE098E0CF2D-E3C8FB24', 3, '15ml liker Tia Maria')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172331-00000FE41AD39D9B-45745C30', '20081130172316-00000FE098E0CF2D-E3C8FB24', 4, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172409-00000FECE6D93F63-6014E882', '20081130172332-00000FE456601EC3-E7D38CF4', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172409-00000FECE6F914FE-048AFB29', '20081130172332-00000FE456601EC3-E7D38CF4', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172409-00000FECE712B157-B3684765', '20081130172332-00000FE456601EC3-E7D38CF4', 3, '125ml sok pomidorowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172409-00000FECE72B1E51-C228FE39', '20081130172332-00000FE456601EC3-E7D38CF4', 4, '1 do 5 zakropienia sosem Tabasco (w zale¿noœci od upodobañ)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172409-00000FECE74332E7-813A1646', '20081130172332-00000FE456601EC3-E7D38CF4', 5, '3 zakropienie sosem Worcestershire')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172409-00000FECE75B6951-19E26456', '20081130172332-00000FE456601EC3-E7D38CF4', 6, 'opcjonalnie ga³¹zka selera')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172409-00000FECE7731752-2B89D628', '20081130172332-00000FE456601EC3-E7D38CF4', 7, 'sól')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172409-00000FECE78AEECB-6733744C', '20081130172332-00000FE456601EC3-E7D38CF4', 8, 'pieprz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172430-00000FF1DC50B10B-A1FD4AE7', '20081130172416-00000FEE786843DE-7AD400C4', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172430-00000FF1DC6E6503-5281C2E3', '20081130172416-00000FEE786843DE-7AD400C4', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172430-00000FF1DC86AF11-789757BA', '20081130172416-00000FEE786843DE-7AD400C4', 3, '25ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172430-00000FF1DC9EFB4F-8F5253CD', '20081130172416-00000FEE786843DE-7AD400C4', 4, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172430-00000FF1DCB6C152-FFAFEF71', '20081130172416-00000FEE786843DE-7AD400C4', 5, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172447-00000FF5A9419AFA-2BD09E15', '20081130172432-00000FF2333C5C28-FA0BD2E6', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172447-00000FF5A95EC115-FCD5F1B9', '20081130172432-00000FF2333C5C28-FA0BD2E6', 2, '25ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172447-00000FF5A9774B86-FCB0DCC5', '20081130172432-00000FF2333C5C28-FA0BD2E6', 3, '25ml Curacao niebieskie')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172447-00000FF5A990608E-ECA631AF', '20081130172432-00000FF2333C5C28-FA0BD2E6', 4, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172505-00000FF9E16E5ED2-B5C08291', '20081130172449-00000FF63F2391F2-F7348277', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172505-00000FF9E18A9EAA-4C51043B', '20081130172449-00000FF63F2391F2-F7348277', 2, '25ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172505-00000FF9E1A2E115-2C3477D3', '20081130172449-00000FF63F2391F2-F7348277', 3, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172505-00000FF9E22E9775-B8C0E9AE', '20081130172449-00000FF63F2391F2-F7348277', 4, '25ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172524-00000FFE5BC7069F-A33570FB', '20081130172508-00000FFA7FBED770-2655822E', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172524-00000FFE5BE212BB-E3A3AC6D', '20081130172508-00000FFA7FBED770-2655822E', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172524-00000FFE5BFA1D7E-C6D8EA78', '20081130172508-00000FFA7FBED770-2655822E', 3, '100ml lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172524-00000FFE5C185D25-6A216BA3', '20081130172508-00000FFA7FBED770-2655822E', 4, '5ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172524-00000FFE5C2FDA0B-159483AC', '20081130172508-00000FFA7FBED770-2655822E', 5, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172544-00001002E6041F96-A0878C0F', '20081130172527-00000FFF162DB455-5D62CD79', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172544-00001002E621AE74-B019345A', '20081130172527-00000FFF162DB455-5D62CD79', 2, '25ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172544-00001002E63A07C9-378EC59B', '20081130172527-00000FFF162DB455-5D62CD79', 3, '10ml Campari')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172544-00001002E651421E-26744CD2', '20081130172527-00000FFF162DB455-5D62CD79', 4, '25ml lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172544-00001002E668BAA6-B5BE66D4', '20081130172527-00000FFF162DB455-5D62CD79', 5, '25ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172544-00001002E67FABDD-573BB34A', '20081130172527-00000FFF162DB455-5D62CD79', 6, '25ml sok z marakui')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172544-00001002E697257D-11AA5BDF', '20081130172527-00000FFF162DB455-5D62CD79', 7, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172544-00001002E6B03857-7A882386', '20081130172527-00000FFF162DB455-5D62CD79', 8, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172600-00001006BBA883EB-0766C4FD', '20081130172545-000010032B714F2F-AE0992F1', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172600-00001006BBC29DC4-A8D7A76D', '20081130172545-000010032B714F2F-AE0992F1', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172600-00001006BBDA430A-87399544', '20081130172545-000010032B714F2F-AE0992F1', 3, '125ml sok ¿urawinowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172600-00001006BBF1B0A9-9B387D1A', '20081130172545-000010032B714F2F-AE0992F1', 4, '1 lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172617-0000100A93C2712F-83ECF1A8', '20081130172601-00001007067201E1-F9CC1B33', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172617-0000100A93E02410-90318DB0', '20081130172601-00001007067201E1-F9CC1B33', 2, '25ml wódka chilli')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172617-0000100A93F89796-030E7411', '20081130172601-00001007067201E1-F9CC1B33', 3, '5ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172617-0000100A9410330A-16C58C71', '20081130172601-00001007067201E1-F9CC1B33', 4, '1 oliwka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172617-0000100A94284F43-5606E472', '20081130172601-00001007067201E1-F9CC1B33', 5, '2 chilli')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172636-0000100F042F15F2-6EB279C0', '20081130172618-0000100AD9E62381-7C1D353B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172636-0000100F044C4B54-F752372E', '20081130172618-0000100AD9E62381-7C1D353B', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172636-0000100F04647903-4245941C', '20081130172618-0000100AD9E62381-7C1D353B', 3, '25ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172636-0000100F047C7022-153BB69C', '20081130172618-0000100AD9E62381-7C1D353B', 4, '25ml sok ¿urawinowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172636-0000100F0494B05E-C5D496D8', '20081130172618-0000100AD9E62381-7C1D353B', 5, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172636-0000100F04AC05FB-1249D02C', '20081130172618-0000100AD9E62381-7C1D353B', 6, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172655-000010139A5664C8-7B90D7CC', '20081130172637-0000100F61908D6F-D34549E5', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172655-000010139A73D975-9FED8EFE', '20081130172637-0000100F61908D6F-D34549E5', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172655-000010139A8C6843-63A1E41D', '20081130172637-0000100F61908D6F-D34549E5', 3, '100ml sok pomidorowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172716-0000101875FC3969-9813626E', '20081130172656-00001013D80F7E26-BC6604BE', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172716-0000101876196DB3-BF3BAE09', '20081130172656-00001013D80F7E26-BC6604BE', 2, '75ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172716-000010187631ACD8-792B6513', '20081130172656-00001013D80F7E26-BC6604BE', 3, '25ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172716-0000101876490C47-06104568', '20081130172656-00001013D80F7E26-BC6604BE', 4, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172716-000010187660F536-CE28E114', '20081130172656-00001013D80F7E26-BC6604BE', 5, '100ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172716-0000101876786DBE-30204074', '20081130172656-00001013D80F7E26-BC6604BE', 6, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172716-0000101876914264-2560EC0E', '20081130172656-00001013D80F7E26-BC6604BE', 7, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172733-0000101C6FEF3043-8C833C76', '20081130172717-00001018C26E9CCC-678A1A47', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172733-0000101C700CF49A-24C7A7BE', '20081130172717-00001018C26E9CCC-678A1A47', 2, '50ml wódka pieprzowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172733-0000101C70258251-658B0149', '20081130172717-00001018C26E9CCC-678A1A47', 3, '10ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172755-000010216B59ED10-AAA0A6AA', '20081130172735-0000101CBF9F692D-BC7AE1D4', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172755-000010216B772C44-2E164D37', '20081130172735-0000101CBF9F692D-BC7AE1D4', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172755-000010216B92E06E-1C5C6B4C', '20081130172735-0000101CBF9F692D-BC7AE1D4', 3, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172755-000010216BABB62B-1340750E', '20081130172735-0000101CBF9F692D-BC7AE1D4', 4, '100ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172755-000010216BC360E6-AFE986CD', '20081130172735-0000101CBF9F692D-BC7AE1D4', 5, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172816-000010264656106A-20117A6A', '20081130172756-00001021CF67E41A-189C65C7', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172816-000010264673CC06-55A9E628', '20081130172756-00001021CF67E41A-189C65C7', 2, '25ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172816-00001026468C36D1-309A9A68', '20081130172756-00001021CF67E41A-189C65C7', 3, '10ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172816-0000102646A42BC1-42B79C3D', '20081130172756-00001021CF67E41A-189C65C7', 4, '15ml krem malinowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172816-0000102646BC0680-4139375E', '20081130172756-00001021CF67E41A-189C65C7', 5, 'malina')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172834-0000102A9CF35CB4-2AA68DD9', '20081130172817-0000102697AF4A09-E053B93F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172834-0000102A9D10FF37-89AAF51A', '20081130172817-0000102697AF4A09-E053B93F', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172834-0000102A9D29A1AA-19557147', '20081130172817-0000102697AF4A09-E053B93F', 3, '15ml Campari')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172834-0000102A9D41A4C9-CDC37F84', '20081130172817-0000102697AF4A09-E053B93F', 4, '50ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172834-0000102A9D5990FE-370B622A', '20081130172817-0000102697AF4A09-E053B93F', 5, 'skórka pomarañcza')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172852-0000102EB75585C8-F06D9400', '20081130172836-0000102AF87FE70A-7A704782', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172852-0000102EB772C72B-FA1D51AD', '20081130172836-0000102AF87FE70A-7A704782', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172852-0000102EB79BE132-467A5020', '20081130172836-0000102AF87FE70A-7A704782', 3, 'angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172852-0000102EB7BC2A79-749AAFCA', '20081130172836-0000102AF87FE70A-7A704782', 4, '25ml Bededictine')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172908-000010328A3C2485-04713D22', '20081130172853-0000102F120081BF-437E1053', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172908-000010328A5DCD30-4C519C58', '20081130172853-0000102F120081BF-437E1053', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172908-000010328A7B1865-F133E460', '20081130172853-0000102F120081BF-437E1053', 3, '5ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172908-000010328A93F169-5113266C', '20081130172853-0000102F120081BF-437E1053', 4, '25ml Galliano')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172925-000010366477C288-EB996FFD', '20081130172910-00001032E97A7D79-DFDD660A', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172925-00001036649199CF-1398EAE1', '20081130172910-00001032E97A7D79-DFDD660A', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172925-0000103664A99205-0E7672BE', '20081130172910-00001032E97A7D79-DFDD660A', 3, '50ml gazowany napój wiœniowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172925-0000103664C17122-B559492D', '20081130172910-00001032E97A7D79-DFDD660A', 4, '50ml sok z winogron')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172925-0000103664D90609-CA7FFF3C', '20081130172910-00001032E97A7D79-DFDD660A', 5, 'winogrona')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172925-0000103664F159EA-1D769B1E', '20081130172910-00001032E97A7D79-DFDD660A', 6, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172942-0000103A512016C4-110C8261', '20081130172926-00001036A99A9FD6-CE9FDD57', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172942-0000103A513D5827-AEEEB00D', '20081130172926-00001036A99A9FD6-CE9FDD57', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172942-0000103A515549D1-662B5036', '20081130172926-00001036A99A9FD6-CE9FDD57', 3, '25ml Chartreuse zielony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172959-0000103E55035ED7-231E2CDD', '20081130172943-0000103AA33254C8-5B1DFCB0', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172959-0000103E552203FB-7B328D92', '20081130172943-0000103AA33254C8-5B1DFCB0', 2, '25ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172959-0000103E553B6567-E3DEAB8C', '20081130172943-0000103AA33254C8-5B1DFCB0', 3, '60ml lody waniliowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172959-0000103E55531F69-62D2F2BC', '20081130172943-0000103AA33254C8-5B1DFCB0', 4, '25ml Kahlua')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130172959-0000103E556A9F95-D7E25F21', '20081130172943-0000103AA33254C8-5B1DFCB0', 5, 'czekolada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173014-00001041DFC40415-3D0A1641', '20081130173000-0000103E9CF1F460-42C63B0F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173014-00001041DFE1C2F7-62A3F602', '20081130173000-0000103E9CF1F460-42C63B0F', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173014-00001041DFFB7EF6-BF08D924', '20081130173000-0000103E9CF1F460-42C63B0F', 3, '100ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173014-00001041E0135440-9E7DE054', '20081130173000-0000103E9CF1F460-42C63B0F', 4, '15ml Galliano')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173014-00001041E02AD7B2-BCF1E3CE', '20081130173000-0000103E9CF1F460-42C63B0F', 5, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173030-00001045AA0FA5C5-A1834608', '20081130173015-0000104231A6377B-92C8AB2F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173030-00001045AA2D0E71-F059132E', '20081130173015-0000104231A6377B-92C8AB2F', 2, '75ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173030-00001045AA454EAD-B989C61A', '20081130173015-0000104231A6377B-92C8AB2F', 3, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173030-00001045AA5F01F1-4ABEF170', '20081130173015-0000104231A6377B-92C8AB2F', 4, '25ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173030-00001045AA7814CB-3D029C3D', '20081130173015-0000104231A6377B-92C8AB2F', 5, '25ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173030-00001045AA8F93DF-CC8E6B61', '20081130173015-0000104231A6377B-92C8AB2F', 6, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173030-00001045AAA72581-C707376F', '20081130173015-0000104231A6377B-92C8AB2F', 7, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173049-0000104A11E07D0C-A2A7B9B1', '20081130173032-00001045FB1411AA-32BA9084', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173049-0000104A11FD0948-22ADEBE1', '20081130173032-00001045FB1411AA-32BA9084', 2, '25ml Cherry Polish')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173049-0000104A1214AE8E-5B6C9AC6', '20081130173032-00001045FB1411AA-32BA9084', 3, '25ml Stolicznaja wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173049-0000104A123064E7-806AD0B3', '20081130173032-00001045FB1411AA-32BA9084', 4, '50ml tonik')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173049-0000104A124AA720-D5546AB9', '20081130173032-00001045FB1411AA-32BA9084', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173110-0000104F00102201-6AEC6BF5', '20081130173051-0000104A91927EC9-07804088', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173110-0000104F00324087-1F5E0F03', '20081130173051-0000104A91927EC9-07804088', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173110-0000104F004C3CE9-DA857475', '20081130173051-0000104A91927EC9-07804088', 3, '5ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173131-00001053B20361D3-AFA6640D', '20081130173112-0000104F4C34FE76-BCF2EE5F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173131-00001053B220D451-EC06F37E', '20081130173112-0000104F4C34FE76-BCF2EE5F', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173131-00001053B238CC87-7E643528', '20081130173112-0000104F4C34FE76-BCF2EE5F', 3, '25ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173131-00001053B2504EE2-B81F5671', '20081130173112-0000104F4C34FE76-BCF2EE5F', 4, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173131-00001053B26760D6-FDBA0337', '20081130173112-0000104F4C34FE76-BCF2EE5F', 5, 'oliwka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173155-0000105962C6F612-FBACB124', '20081130173132-00001054007B6FF2-9DE3F822', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173155-0000105962E5109E-243E7EAE', '20081130173132-00001054007B6FF2-9DE3F822', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173155-0000105962FD1933-D56566EA', '20081130173132-00001054007B6FF2-9DE3F822', 3, '15ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173155-00001059631672FB-8141FF1B', '20081130173132-00001054007B6FF2-9DE3F822', 4, '25ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173155-0000105963311425-5E0E54A6', '20081130173132-00001054007B6FF2-9DE3F822', 5, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173216-0000105E4FA5FB9E-917A833B', '20081130173201-0000105AA92CE4DC-86FE618D', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173216-0000105E4FC1B99A-5CD37047', '20081130173201-0000105AA92CE4DC-86FE618D', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173216-0000105E4FD9E403-A5CD93BB', '20081130173201-0000105AA92CE4DC-86FE618D', 3, '5ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173216-0000105E4FF14C2D-E1C92127', '20081130173201-0000105AA92CE4DC-86FE618D', 4, '10ml creme de cassis')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173216-0000105E500990C7-48A4D31B', '20081130173201-0000105AA92CE4DC-86FE618D', 5, '100ml piwo imbirowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173216-0000105E50235AF6-509478E8', '20081130173201-0000105AA92CE4DC-86FE618D', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173235-00001062AA383264-11D3DD5F', '20081130173217-0000105E902AC24D-A0C74324', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173235-00001062AA55B312-92E930E1', '20081130173217-0000105E902AC24D-A0C74324', 2, '10ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173235-00001062AA6EE133-7217211C', '20081130173217-0000105E902AC24D-A0C74324', 3, '20ml creme de cassis')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173235-00001062AA867B90-6E464384', '20081130173217-0000105E902AC24D-A0C74324', 4, '100ml wino musuj¹ce lub szampan')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173235-00001062AA9E06A6-6900AA79', '20081130173217-0000105E902AC24D-A0C74324', 5, 'rodzynki')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173254-00001067307C21F8-58CC51CB', '20081130173236-00001062F48FA7C9-8556E99C', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173254-000010673098C865-A0315C05', '20081130173236-00001062F48FA7C9-8556E99C', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173254-0000106730B71CC8-80C2DC71', '20081130173236-00001062F48FA7C9-8556E99C', 3, '15ml Campari')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173254-0000106730CF9E7E-19DBF746', '20081130173236-00001062F48FA7C9-8556E99C', 4, '15ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173254-0000106730E720D8-5109D3F4', '20081130173236-00001062F48FA7C9-8556E99C', 5, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173311-0000106B11F317FD-E7DFC24D', '20081130173256-000010677E55698F-28C6DAEF', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173311-0000106B120D9869-CC1C5CD9', '20081130173256-000010677E55698F-28C6DAEF', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173311-0000106B1225972C-83485947', '20081130173256-000010677E55698F-28C6DAEF', 3, '50ml sok z mango')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173311-0000106B12433AC5-7FFC3C7A', '20081130173256-000010677E55698F-28C6DAEF', 4, '15ml likier melonowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173311-0000106B125D3DB3-7797E0DA', '20081130173256-000010677E55698F-28C6DAEF', 5, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173311-0000106B12754302-253CEA48', '20081130173256-000010677E55698F-28C6DAEF', 6, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173311-0000106B128CECA5-6BA472FB', '20081130173256-000010677E55698F-28C6DAEF', 7, 'mango')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173329-0000106F3445A238-69B87B8E', '20081130173312-0000106B63656B01-B94E05FB', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173329-0000106F34604F63-D859883F', '20081130173312-0000106B63656B01-B94E05FB', 2, '15ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173329-0000106F3478456A-F6793AFD', '20081130173312-0000106B63656B01-B94E05FB', 3, '15ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173329-0000106F348FC8DC-450AE224', '20081130173312-0000106B63656B01-B94E05FB', 4, '15ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173329-0000106F34A752DA-BD5D1F9C', '20081130173312-0000106B63656B01-B94E05FB', 5, '15ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173329-0000106F34BEF709-9FCC20FD', '20081130173312-0000106B63656B01-B94E05FB', 6, '15ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173329-0000106F34D61A73-E4252D46', '20081130173312-0000106B63656B01-B94E05FB', 7, '15ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173329-0000106F34ED85E3-9B613582', '20081130173312-0000106B63656B01-B94E05FB', 8, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173329-0000106F350593EC-4851A2A2', '20081130173312-0000106B63656B01-B94E05FB', 9, '100ml cola')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173329-0000106F351D3EA7-953B407F', '20081130173312-0000106B63656B01-B94E05FB', 10, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173348-00001073CD117C83-230B735F', '20081130173330-0000106F81425A68-A1879C85', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173348-00001073CD2E7D83-8493568E', '20081130173330-0000106F81425A68-A1879C85', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173348-00001073CD461D55-63B07093', '20081130173330-0000106F81425A68-A1879C85', 3, '50ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173349-00001073CD5E098A-D99D0F73', '20081130173330-0000106F81425A68-A1879C85', 4, '50ml sok ¿urawionowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173407-000010781D0FED50-5DE71DA1', '20081130173350-000010741D78B48E-3C1A7B46', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173407-000010781D2D3F12-C75514BC', '20081130173350-000010741D78B48E-3C1A7B46', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173407-000010781D450FFF-4009A798', '20081130173350-000010741D78B48E-3C1A7B46', 3, '15ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173407-000010781D5C8F13-C25B5022', '20081130173350-000010741D78B48E-3C1A7B46', 4, '15ml creme de menthe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173407-000010781D73961D-08C480C9', '20081130173350-000010741D78B48E-3C1A7B46', 5, '25ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173407-000010781D8AEDE9-50F88D52', '20081130173350-000010741D78B48E-3C1A7B46', 6, '100ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173407-000010781DA1FEC6-17897EF3', '20081130173350-000010741D78B48E-3C1A7B46', 7, 'miêta')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173407-000010781DE8FDDF-BC132AFE', '20081130173350-000010741D78B48E-3C1A7B46', 8, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173426-0000107C942BEA99-957080AA', '20081130173408-000010786A6EA39E-776BDF57', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173426-0000107C9445D010-2774FAB0', '20081130173408-000010786A6EA39E-776BDF57', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173426-0000107C945DAF2D-DD9B33DC', '20081130173408-000010786A6EA39E-776BDF57', 3, '75ml piwo imbirowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173426-0000107C9475535C-D55F4BF4', '20081130173408-000010786A6EA39E-776BDF57', 4, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173426-0000107C948D8568-9C1D1056', '20081130173408-000010786A6EA39E-776BDF57', 5, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173446-00001081495E71E3-7A744867', '20081130173428-0000107CF0AEAFF5-69F0C942', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173446-00001081497C1D20-A385358B', '20081130173428-0000107CF0AEAFF5-69F0C942', 2, '50ml wódka Stolicznaja')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173446-000010814994BF93-9E1E28CD', '20081130173428-0000107CF0AEAFF5-69F0C942', 3, '15ml Curacao pomarañczowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173446-0000108149AC9B69-209A8FCC', '20081130173428-0000107CF0AEAFF5-69F0C942', 4, '15ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173446-0000108149C40F94-76C1A7A3', '20081130173428-0000107CF0AEAFF5-69F0C942', 5, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173446-0000108149DD0FE1-2F87991E', '20081130173428-0000107CF0AEAFF5-69F0C942', 6, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173503-000010853C090571-2EB01C89', '20081130173448-000010818A773DD4-DEFE17CA', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173503-000010853C26884E-ACFEE91B', '20081130173448-000010818A773DD4-DEFE17CA', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173503-000010853C3E676B-8D795E61', '20081130173448-000010818A773DD4-DEFE17CA', 3, '25ml cola')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173503-000010853C59A5B9-0B0655C5', '20081130173448-000010818A773DD4-DEFE17CA', 4, '25ml creme de cacao')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173521-0000108953C5DC74-34D49E16', '20081130173505-000010859981AF9F-7A6E1879', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173521-0000108953E304BD-EFEBFAED', '20081130173505-000010859981AF9F-7A6E1879', 2, '30ml wódka Wyborowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173521-0000108953FAAF78-0F742808', '20081130173505-000010859981AF9F-7A6E1879', 3, '10ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173521-000010895415149D-7D5749D0', '20081130173505-000010859981AF9F-7A6E1879', 4, '10ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173521-00001089542DAE55-7D954291', '20081130173505-000010859981AF9F-7A6E1879', 5, '10ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173521-000010895445C2EA-B96F48D2', '20081130173505-000010859981AF9F-7A6E1879', 6, 'grejpfrut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173537-0000108D15770EF3-896F5AFC', '20081130173523-00001089DFD3B722-286528CB', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173537-0000108D1594516E-9F3FFEA9', '20081130173523-00001089DFD3B722-286528CB', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173537-0000108D15AC86C0-CB6B57E3', '20081130173523-00001089DFD3B722-286528CB', 3, '10ml Cointeau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173537-0000108D15C4E35C-21EAE687', '20081130173523-00001089DFD3B722-286528CB', 4, '10ml œliwowica')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173537-0000108D15DED076-54D71F84', '20081130173523-00001089DFD3B722-286528CB', 5, '10ml creme de mure')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173537-0000108D15F74FFE-8521ACE0', '20081130173523-00001089DFD3B722-286528CB', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173553-00001090B567575C-D584CA86', '20081130173538-0000108D4F1C9BF8-3EB53305', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173553-00001090B5850FB2-EFAAC87B', '20081130173538-0000108D4F1C9BF8-3EB53305', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173553-00001090B5A005FA-40AB725E', '20081130173538-0000108D4F1C9BF8-3EB53305', 3, '50ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173553-00001090B5BF43E5-F543868B', '20081130173538-0000108D4F1C9BF8-3EB53305', 4, '50ml sok winogronowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173553-00001090B5D9561A-7ED91EA6', '20081130173538-0000108D4F1C9BF8-3EB53305', 5, 'cukier puder')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173553-00001090B5F12B64-6F9695A2', '20081130173538-0000108D4F1C9BF8-3EB53305', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173725-000010A622F41C4D-F1E3EE9A', '20081130173713-000010A36482536A-99D59C9A', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173725-000010A6232021D8-4C24262E', '20081130173713-000010A36482536A-99D59C9A', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173725-000010A6233B4A54-2FE6F97F', '20081130173713-000010A36482536A-99D59C9A', 3, '100ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173725-000010A623541527-E7650724', '20081130173713-000010A36482536A-99D59C9A', 4, '50ml sól')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173725-000010A6236C43ED-8CC80EDE', '20081130173713-000010A36482536A-99D59C9A', 5, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173742-000010AA1AEA9932-CA06E682', '20081130173729-000010A71F98C9AF-7C89B069', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173742-000010AA1B082A3E-6159360C', '20081130173729-000010A71F98C9AF-7C89B069', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173742-000010AA1B1FD1B4-72FCB1EC', '20081130173729-000010A71F98C9AF-7C89B069', 3, '100ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173742-000010AA1B36BB47-851BEAB4', '20081130173729-000010A71F98C9AF-7C89B069', 4, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173756-000010AD7604BCFA-2555C47F', '20081130173743-000010AA51520AD9-0F08B95F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173756-000010AD7622CB85-41F97797', '20081130173743-000010AA51520AD9-0F08B95F', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173756-000010AD763B8085-CF463746', '20081130173743-000010AA51520AD9-0F08B95F', 3, '75ml sok ¿urawinowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173756-000010AD765331CD-461F45B5', '20081130173743-000010AA51520AD9-0F08B95F', 4, '50ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173756-000010AD766A73C5-13AAD235', '20081130173743-000010AA51520AD9-0F08B95F', 5, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173811-000010B10416861B-F994E3B2', '20081130173757-000010ADB691490B-43B13458', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173811-000010B104340383-411C0FD9', '20081130173757-000010ADB691490B-43B13458', 2, '25ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173811-000010B1044BD470-F80583F4', '20081130173757-000010ADB691490B-43B13458', 3, '50ml sok ¿urawinowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173811-000010B10463F1C0-EB6575C3', '20081130173757-000010ADB691490B-43B13458', 4, '75ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173811-000010B1047B0B58-AC4B8CDC', '20081130173757-000010ADB691490B-43B13458', 5, '25ml napój gazowany')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173811-000010B1049281B1-D40637AE', '20081130173757-000010ADB691490B-43B13458', 6, 'ananas')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173811-000010B104A9905F-B2F806C8', '20081130173757-000010ADB691490B-43B13458', 7, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173829-000010B507F7C25F-0135C1AD', '20081130173813-000010B1549E9662-E170AAA1', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173829-000010B508166554-5C7F4AC7', '20081130173813-000010B1549E9662-E170AAA1', 2, '25ml wódka cytrynowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173829-000010B508300722-FA5C48C3', '20081130173813-000010B1549E9662-E170AAA1', 3, '50ml sok mandarynkowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173829-000010B508479D21-A30AC994', '20081130173813-000010B1549E9662-E170AAA1', 4, '25ml brzoskwionowy schnapps')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173829-000010B5085F137B-9B271278', '20081130173813-000010B1549E9662-E170AAA1', 5, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173829-000010B5087689D4-B7E28EEE', '20081130173813-000010B1549E9662-E170AAA1', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173845-000010B8C701F705-6D8FB368', '20081130173830-000010B557BB814D-200E7DAB', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173845-000010B8C71F580E-F3DB8D60', '20081130173830-000010B557BB814D-200E7DAB', 2, '30ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173845-000010B8C737249D-19701A74', '20081130173830-000010B557BB814D-200E7DAB', 3, '50ml sok mandarynkowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173845-000010B8C74EFAFF-C7C244EE', '20081130173830-000010B557BB814D-200E7DAB', 4, '15ml sherry')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173845-000010B8C7668615-6B6FD73A', '20081130173830-000010B557BB814D-200E7DAB', 5, '15ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173845-000010B8C77E6F03-DC03388C', '20081130173830-000010B557BB814D-200E7DAB', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173903-000010BD1CACB953-7F62739C', '20081130173846-000010B90C22BA55-33B83F1D', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173903-000010BD1CC6CECE-011EB09E', '20081130173846-000010B90C22BA55-33B83F1D', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173903-000010BD1CDEB131-F0F86B21', '20081130173846-000010B90C22BA55-33B83F1D', 3, '50ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173903-000010BD1CF6E683-2A7CC8EA', '20081130173846-000010B90C22BA55-33B83F1D', 4, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173919-000010C0C2DAD275-9F4A7CFC', '20081130173905-000010BD78A5D765-0C7E9C63', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173919-000010C0C2F83D50-938CAED7', '20081130173905-000010BD78A5D765-0C7E9C63', 2, '50ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173919-000010C0C3103011-D90D5DDB', '20081130173905-000010BD78A5D765-0C7E9C63', 3, '15ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173919-000010C0C327B26C-7A823847', '20081130173905-000010BD78A5D765-0C7E9C63', 4, '15ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173919-000010C0C33FA873-05E5ED71', '20081130173905-000010BD78A5D765-0C7E9C63', 5, '5ml gorzka wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173919-000010C0C357003F-5500F50C', '20081130173905-000010BD78A5D765-0C7E9C63', 6, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173919-000010C0C36E3407-B23C90F0', '20081130173905-000010BD78A5D765-0C7E9C63', 7, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173935-000010C48A7FE255-E83F20FD', '20081130173920-000010C10313E9A5-27A71F23', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173935-000010C48A9C59D5-3652E806', '20081130173920-000010C10313E9A5-27A71F23', 2, '25ml wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173935-000010C48AB69E3D-590CDED9', '20081130173920-000010C10313E9A5-27A71F23', 3, '100ml sok ¿urawiny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173935-000010C48ACEEED8-656527FD', '20081130173920-000010C10313E9A5-27A71F23', 4, '30ml gazowany napój brzoskwiniowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130173935-000010C48AF36F63-2C68896F', '20081130173920-000010C10313E9A5-27A71F23', 5, 'brzoskwinia')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174157-000010E59DC37D36-6E7E6A17', '20081130174142-000010E217BDDBF3-B852AFB8', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174157-000010E59DEB29A9-C401E575', '20081130174142-000010E217BDDBF3-B852AFB8', 2, '50ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174157-000010E59E061FF1-48E1B3B4', '20081130174142-000010E217BDDBF3-B852AFB8', 3, '5ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174157-000010E59E1EFD52-6B083165', '20081130174142-000010E217BDDBF3-B852AFB8', 4, '100ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174157-000010E59E3763C0-2C517603', '20081130174142-000010E217BDDBF3-B852AFB8', 5, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174218-000010EA855311DA-02477C5A', '20081130174159-000010E5F7289E51-54DEF209', 1, 'Lód tuczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174218-000010EA8570CC5E-75100FDC', '20081130174159-000010E5F7289E51-54DEF209', 2, '25ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174218-000010EA8588EAC6-FA6DA908', '20081130174159-000010E5F7289E51-54DEF209', 3, '15ml creme de framboise')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174218-000010EA85A12B02-79DBC78B', '20081130174159-000010E5F7289E51-54DEF209', 4, '15ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174218-000010EA85B91A7D-9B1D60DE', '20081130174159-000010E5F7289E51-54DEF209', 5, '15ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174218-000010EA85D141A0-5867C5A4', '20081130174159-000010E5F7289E51-54DEF209', 6, '15ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174218-000010EA85E90D18-6F4B32D1', '20081130174159-000010E5F7289E51-54DEF209', 7, '100ml wiœniowego napoju gazowanego')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174218-000010EA860EB78E-274405A6', '20081130174159-000010E5F7289E51-54DEF209', 8, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174218-000010EA862A84D1-D970F6F0', '20081130174159-000010E5F7289E51-54DEF209', 9, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174237-000010EED7F91C8D-A252D893', '20081130174220-000010EADCC83554-4922CFCB', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174237-000010EED815A7B2-8414BCA9', '20081130174220-000010EADCC83554-4922CFCB', 2, '10ml z³ota tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174237-000010EED82E4A25-92E11E34', '20081130174220-000010EADCC83554-4922CFCB', 3, '10ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174237-000010EED84646B8-BB6FF4A3', '20081130174220-000010EADCC83554-4922CFCB', 4, '10ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174237-000010EED85DC170-C81CFE4F', '20081130174220-000010EADCC83554-4922CFCB', 5, '5ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174253-000010F2827D9FB2-7FF34B40', '20081130174238-000010EF2473F2D6-D5E3893B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174253-000010F2829A5337-D6E20B91', '20081130174238-000010EF2473F2D6-D5E3893B', 2, '50ml z³ota tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174253-000010F282B2888A-68A4724C', '20081130174238-000010EF2473F2D6-D5E3893B', 3, '25ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174253-000010F282CCBC93-A5047389', '20081130174238-000010EF2473F2D6-D5E3893B', 4, '5ml Drambuie')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174253-000010F282E545ED-76B6994E', '20081130174238-000010EF2473F2D6-D5E3893B', 5, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174253-000010F282FCED62-8BAD69DE', '20081130174238-000010EF2473F2D6-D5E3893B', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174308-000010F621C4D2E1-65F4FBD0', '20081130174254-000010F2C5DC113E-6164C25D', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174308-000010F621E2BB3B-8B5DAC08', '20081130174254-000010F2C5DC113E-6164C25D', 2, '30ml srebna tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174308-000010F621FB1A05-6F64B1C6', '20081130174254-000010F2C5DC113E-6164C25D', 3, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174308-000010F62214576E-AEAD8431', '20081130174254-000010F2C5DC113E-6164C25D', 4, '15ml krem kokosowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174308-000010F6222FE338-6746AF05', '20081130174254-000010F2C5DC113E-6164C25D', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174324-000010F9D8DE4F14-418F76C1', '20081130174309-000010F669208687-A06986B6', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174324-000010F9D8FC6543-BAD03C19', '20081130174309-000010F669208687-A06986B6', 2, '40ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174324-000010F9D91783EC-3CC863E9', '20081130174309-000010F669208687-A06986B6', 3, '15ml sok jab³kowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174324-000010F9D930854F-6B3F534A', '20081130174309-000010F669208687-A06986B6', 4, '15ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174324-000010F9D948C58B-99613DB9', '20081130174309-000010F669208687-A06986B6', 5, 'jab³ko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174340-000010FD6B915201-D33CE68D', '20081130174326-000010FA30A24AAF-CEC61225', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174340-000010FD6BAE6C1A-60D4799D', '20081130174326-000010FA30A24AAF-CEC61225', 2, '40ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174340-000010FD6BC6EBA2-0061D6FA', '20081130174326-000010FA30A24AAF-CEC61225', 3, '5ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174340-000010FD6BDEF77C-A29944E2', '20081130174326-000010FA30A24AAF-CEC61225', 4, '50ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174340-000010FD6BF6CA98-2D1831A5', '20081130174326-000010FA30A24AAF-CEC61225', 5, '100ml cola')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174340-000010FD6C0E4895-8D1ACFDD', '20081130174326-000010FA30A24AAF-CEC61225', 6, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174354-00001100DD2C05D7-68E589E1', '20081130174341-000010FDB752AE0A-6ED2A739', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174354-00001100DD60C0D9-2433A3DC', '20081130174341-000010FDB752AE0A-6ED2A739', 2, '50ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174354-00001100DD7C20FC-3A7E65F8', '20081130174341-000010FDB752AE0A-6ED2A739', 3, '15ml wermut wytrawny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174354-00001100DD97B698-C5A11CFF', '20081130174341-000010FDB752AE0A-6ED2A739', 4, '25ml wermut s³odki')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174354-00001100DDB06B98-C5286F16', '20081130174341-000010FDB752AE0A-6ED2A739', 5, '50ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174354-00001100DDC85F71-6656AE49', '20081130174341-000010FDB752AE0A-6ED2A739', 6, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174410-000011047A778C3A-3025AB80', '20081130174355-000011011193324E-17F22126', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174410-000011047A960D5B-743CE782', '20081130174355-000011011193324E-17F22126', 2, '40ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174410-000011047AB06ADC-8734B189', '20081130174355-000011011193324E-17F22126', 3, '5ml gorzka wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174410-000011047AC8D490-62F80C84', '20081130174355-000011011193324E-17F22126', 4, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174410-000011047AE05C5F-A7CDB6FC', '20081130174355-000011011193324E-17F22126', 5, '100ml tonic')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174410-000011047AF7B313-2739029F', '20081130174355-000011011193324E-17F22126', 6, '5ml cukier puder')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174410-000011047B0F1A26-CA608848', '20081130174355-000011011193324E-17F22126', 7, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174426-000011082E41B9C8-D81614E3', '20081130174411-00001104BF51506F-5988F347', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174426-000011082E5F7563-422E7411', '20081130174411-00001104BF51506F-5988F347', 2, '40ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174426-000011082E784B21-5C963440', '20081130174411-00001104BF51506F-5988F347', 3, '15ml miód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174426-000011082E90B3BD-973DF46D', '20081130174411-00001104BF51506F-5988F347', 4, '40ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174426-000011082EA89DC4-0173EA59', '20081130174411-00001104BF51506F-5988F347', 5, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174443-0000110C2E299501-16A30490', '20081130174427-000011086135A8A2-25370D04', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174443-0000110C2E46F2C3-6924E8B8', '20081130174427-000011086135A8A2-25370D04', 2, '30ml srebrna tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174443-0000110C2E5FC880-31A0D094', '20081130174427-000011086135A8A2-25370D04', 3, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174443-0000110C2E77FEEA-47176863', '20081130174427-000011086135A8A2-25370D04', 4, '10ml creme de banana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174443-0000110C2E8F908C-1F7AC5A6', '20081130174427-000011086135A8A2-25370D04', 5, '50ml lemoniady')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174443-0000110C2EA6F458-42621DCE', '20081130174427-000011086135A8A2-25370D04', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174458-0000110FC4D65901-4879405B', '20081130174444-0000110C7307B8DB-2BC4837D', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174458-0000110FC4F3CFDC-81EBD530', '20081130174444-0000110C7307B8DB-2BC4837D', 2, '50ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174458-0000110FC50C8FC6-3E52E268', '20081130174444-0000110C7307B8DB-2BC4837D', 3, '10ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174458-0000110FC5260A4B-5CF79C87', '20081130174444-0000110C7307B8DB-2BC4837D', 4, '15ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174458-0000110FC5436925-8A9CD95C', '20081130174444-0000110C7307B8DB-2BC4837D', 5, '50ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174513-000011133FB5A786-C8E94EFC', '20081130174500-00001110110DE7C6-36047FC6', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174513-000011133FD20FBF-38CD3E9A', '20081130174500-00001110110DE7C6-36047FC6', 2, '50ml z³ota tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174513-000011133FEA8F46-BDA6333D', '20081130174500-00001110110DE7C6-36047FC6', 3, '15ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174513-000011134003009E-11E7DC36', '20081130174500-00001110110DE7C6-36047FC6', 4, '10ml miód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174513-00001113401AFB03-3CAD5E74', '20081130174500-00001110110DE7C6-36047FC6', 5, '25ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174513-000011134032E620-D17E96C9', '20081130174500-00001110110DE7C6-36047FC6', 6, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174531-000011175AC024AC-8957CCBE', '20081130174514-000011137EF3AAE8-C7F19268', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174531-000011175ADD4BDE-A2714795', '20081130174514-000011137EF3AAE8-C7F19268', 2, '30ml srebrna tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174531-000011175AF8EE92-3F8227CE', '20081130174514-000011137EF3AAE8-C7F19268', 3, '15ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174531-000011175B140D3B-BEC31175', '20081130174514-000011137EF3AAE8-C7F19268', 4, '50ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174531-000011175B2C52EC-3E578AC7', '20081130174514-000011137EF3AAE8-C7F19268', 5, 'sól')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174531-000011175B44F676-CB62F022', '20081130174514-000011137EF3AAE8-C7F19268', 6, 'skórka limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174548-0000111B5801CF35-10C00D26', '20081130174532-00001117A4CEA3E4-AC8F7C90', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174548-0000111B581C9D1C-FAF84BB3', '20081130174532-00001117A4CEA3E4-AC8F7C90', 2, '50ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174548-0000111B5834F32C-458FE7B3', '20081130174532-00001117A4CEA3E4-AC8F7C90', 3, '100ml ginger ale')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174548-0000111B587C04D3-E852CBAD', '20081130174532-00001117A4CEA3E4-AC8F7C90', 4, '5ml Campari')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174548-0000111B5894EF4C-DD8E876B', '20081130174532-00001117A4CEA3E4-AC8F7C90', 5, 's³omka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174603-0000111EDD7C44AF-E8A92965', '20081130174549-0000111BA2ED0634-414142A9', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174603-0000111EDD990207-926B35BB', '20081130174549-0000111BA2ED0634-414142A9', 2, '50ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174603-0000111EDDE1A316-E6DA4674', '20081130174549-0000111BA2ED0634-414142A9', 3, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174603-0000111EDDFB9147-34D6A34F', '20081130174549-0000111BA2ED0634-414142A9', 4, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174603-0000111EDE141415-46303C9B', '20081130174549-0000111BA2ED0634-414142A9', 5, '25ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174632-000011258671DC76-E58E2DDA', '20081130174605-0000111F31EBEF68-DBC3B8D0', 1, '25ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174632-0000112586A00B47-32FCA246', '20081130174605-0000111F31EBEF68-DBC3B8D0', 2, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174632-0000112586BC3F1F-F3AE31D3', '20081130174605-0000111F31EBEF68-DBC3B8D0', 3, 'sól')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174648-00001129575BCAAD-0551EF53', '20081130174634-0000112600644085-F69F16EB', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174648-00001129577BEB94-A8015C5B', '20081130174634-0000112600644085-F69F16EB', 2, '30ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174648-0000112957961911-04E93C06', '20081130174634-0000112600644085-F69F16EB', 3, 'sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174648-0000112957B16B04-74CC2446', '20081130174634-0000112600644085-F69F16EB', 4, '20ml creme de menthe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174648-0000112957CB1F60-21A86548', '20081130174634-0000112600644085-F69F16EB', 5, 'miêta')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174648-0000112957E31ADC-26E9E2ED', '20081130174634-0000112600644085-F69F16EB', 6, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174706-0000112D9181A948-79468D4E', '20081130174649-000011299991C5AC-91D64E42', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174706-0000112D919CED0B-C614DE07', '20081130174649-000011299991C5AC-91D64E42', 2, '25ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174706-0000112D91B7EAF7-6CC89EF4', '20081130174649-000011299991C5AC-91D64E42', 3, '25ml wódka z pieprzem')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174706-0000112D91D07222-3D7FEFEB', '20081130174649-000011299991C5AC-91D64E42', 4, '25ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174706-0000112D91E8130B-04BD45C2', '20081130174649-000011299991C5AC-91D64E42', 5, '25ml sok ¿urawinowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174706-0000112D91FFF456-CB01C5C2', '20081130174649-000011299991C5AC-91D64E42', 6, '25ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174706-0000112D92177482-FE1E3055', '20081130174649-000011299991C5AC-91D64E42', 7, 'papryka chilli')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174726-0000113236E30621-F347A46A', '20081130174708-0000112E0774C941-17A14017', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174726-0000113237014C54-6D5022B1', '20081130174708-0000112E0774C941-17A14017', 2, '25ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174726-00001132371A4ECF-29C95C49', '20081130174708-0000112E0774C941-17A14017', 3, '100ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174726-000011323731E4CE-1A26C19A', '20081130174708-0000112E0774C941-17A14017', 4, '25ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174726-000011323749BF8D-7FAAEDD0', '20081130174708-0000112E0774C941-17A14017', 5, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174752-000011384CEB1FB5-C458F3FA', '20081130174742-00001135FA2839BD-2B5529C7', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174752-000011384D091A9B-96E5E19B', '20081130174742-00001135FA2839BD-2B5529C7', 2, '25ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174752-000011384D2427CE-2DBB4DE8', '20081130174742-00001135FA2839BD-2B5529C7', 3, '5ml Galliano')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174752-000011384D3C1CBE-36F22A23', '20081130174742-00001135FA2839BD-2B5529C7', 4, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174752-000011384D542E0E-1414249D', '20081130174742-00001135FA2839BD-2B5529C7', 5, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174752-000011384D6C1814-37B8E37F', '20081130174742-00001135FA2839BD-2B5529C7', 6, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174811-0000113CB6BD2C40-E9360FD3', '20081130174754-0000113899849D50-5441AFE1', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174811-0000113CB6DAA9A8-3FB23A76', '20081130174754-0000113899849D50-5441AFE1', 2, '40ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174811-0000113CB6F33F03-9B22732D', '20081130174754-0000113899849D50-5441AFE1', 3, '15ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174811-0000113CB70BA688-21B004C6', '20081130174754-0000113899849D50-5441AFE1', 4, '15ml s³odki wermut ')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174811-0000113CB72D3ED4-028B4D17', '20081130174754-0000113899849D50-5441AFE1', 5, '25ml Campari')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174811-0000113CB7466CF6-63865DFD', '20081130174754-0000113899849D50-5441AFE1', 6, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174811-0000113CB75E3DE3-958CB99A', '20081130174754-0000113899849D50-5441AFE1', 7, '5ml gorzka wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174830-000011411C8EF789-C1A97558', '20081130174813-0000113D19FCE460-5C2BA24F', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174830-000011411CD60819-13ED4AAF', '20081130174813-0000113D19FCE460-5C2BA24F', 2, '40ml z³ota tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174830-000011411CEF097D-D79E5DCA', '20081130174813-0000113D19FCE460-5C2BA24F', 3, '15ml sherry')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174830-000011411D075C46-5534C1EA', '20081130174813-0000113D19FCE460-5C2BA24F', 4, 's³omka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174853-000011465237BD2D-FA62E411', '20081130174832-000011418C3B7EC4-CB93CEB1', 1, '25ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174853-0000114652530F20-6DD5FF01', '20081130174832-000011418C3B7EC4-CB93CEB1', 2, '250ml piwo najlepiej z tequil¹')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174911-0000114A7C7D4582-06F4D3D6', '20081130174854-00001146A6430020-344D68EE', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174911-0000114A7C9ADD1B-C2C615A1', '20081130174854-00001146A6430020-344D68EE', 2, '50ml srebrna tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174911-0000114A7CB344A0-62E90523', '20081130174854-00001146A6430020-344D68EE', 3, '100ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174911-0000114A7CCF8C1C-C7E2FDB4', '20081130174854-00001146A6430020-344D68EE', 4, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174925-0000114DF6098C0E-1916196B', '20081130174911-0000114AB207706D-909141EB', 1, '25ml srebrna tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174926-0000114DF627B5E2-4B686108', '20081130174911-0000114AB207706D-909141EB', 2, '25ml lemoniada lub wino musuj¹ce')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174944-000011525A8D00DA-ADD8832E', '20081130174927-0000114E467D8873-54DBED9F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174944-000011525AAAA58B-CF60060E', '20081130174927-0000114E467D8873-54DBED9F', 2, '50ml z³ota tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174944-000011525AC33571-0DC4EB51', '20081130174927-0000114E467D8873-54DBED9F', 3, '50ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174944-000011525ADBCBE3-387F7789', '20081130174927-0000114E467D8873-54DBED9F', 4, '100ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174944-000011525AF3A245-F0AC9864', '20081130174927-0000114E467D8873-54DBED9F', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130174944-000011525B0BF50E-055AFEFB', '20081130174927-0000114E467D8873-54DBED9F', 6, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175000-00001155F947D027-BF775423', '20081130174946-00001152B6BAEFC2-31D34ABB', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175000-00001155F96806E1-C7281324', '20081130174946-00001152B6BAEFC2-31D34ABB', 2, '50ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175000-00001155F9AE7935-6FD85A20', '20081130174946-00001152B6BAEFC2-31D34ABB', 3, '50ml piwo')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175000-00001155F9C78AF7-9BCE6EE2', '20081130174946-00001152B6BAEFC2-31D34ABB', 4, '25ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175000-00001155F9E360F6-98CDC924', '20081130174946-00001152B6BAEFC2-31D34ABB', 5, '25ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175000-00001155F9FD79B7-7D6981C2', '20081130174946-00001152B6BAEFC2-31D34ABB', 6, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175000-00001155FA157990-51A55D5C', '20081130174946-00001152B6BAEFC2-31D34ABB', 7, '5ml cukier puder lub syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175000-00001155FA2DA96E-2E526181', '20081130174946-00001152B6BAEFC2-31D34ABB', 8, '15ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175000-00001155FA4566B6-30508639', '20081130174946-00001152B6BAEFC2-31D34ABB', 9, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175000-00001155FA5CF512-ADC2B039', '20081130174946-00001152B6BAEFC2-31D34ABB', 10, 's³omka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175026-0000115C15CBB586-F4DE6046', '20081130175002-00001156589C2C6C-CDFA2110', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175026-0000115C1612FCA6-8A285602', '20081130175002-00001156589C2C6C-CDFA2110', 2, '50ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175026-0000115C162C71B6-1DB6473A', '20081130175002-00001156589C2C6C-CDFA2110', 3, '25ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175026-0000115C1643ED85-5AF5477B', '20081130175002-00001156589C2C6C-CDFA2110', 4, '100ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175026-0000115C165B61AF-AC1B5950', '20081130175002-00001156589C2C6C-CDFA2110', 5, '10ml creme de cassis')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175026-0000115C1672D5DA-DB47150B', '20081130175002-00001156589C2C6C-CDFA2110', 6, '15ml miód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175043-0000115FEFFF244B-892BCEFB', '20081130175028-0000115C99D84A59-C5A5A885', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175043-0000115FF01BD7D0-8BA41EC5', '20081130175028-0000115C99D84A59-C5A5A885', 2, '50ml tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175043-0000115FF0347188-56C6D0EB', '20081130175028-0000115C99D84A59-C5A5A885', 3, '10ml cukier puder')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175043-0000115FF04C644A-945A00AE', '20081130175028-0000115C99D84A59-C5A5A885', 4, '25ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175043-0000115FF0646769-DFB6FEBC', '20081130175028-0000115C99D84A59-C5A5A885', 5, '5ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175043-0000115FF07C3FFA-F636519B', '20081130175028-0000115C99D84A59-C5A5A885', 6, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175103-00001164B6774F03-9B711166', '20081130175044-0000116040DB423B-43FB04A7', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175103-00001164B69501E3-BEEB3E45', '20081130175044-0000116040DB423B-43FB04A7', 2, '30ml srebrna tequila')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175103-00001164B6DD4BA5-824D96C9', '20081130175044-0000116040DB423B-43FB04A7', 3, '5ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175103-00001164B6F858D8-5FE114F3', '20081130175044-0000116040DB423B-43FB04A7', 4, '25ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175103-00001164B711AAFD-C74FA1BC', '20081130175044-0000116040DB423B-43FB04A7', 5, '25ml œliwowica')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130175103-00001164B729AE1C-B6308B65', '20081130175044-0000116040DB423B-43FB04A7', 6, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183127-000013991A00BAFB-ACDB1C99', '20081130183124-000013984C1AC99E-9CC79721', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183127-000013991A25AD05-77C1ED23', '20081130183124-000013984C1AC99E-9CC79721', 2, '25ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183127-00001399292FD011-85245147', '20081130183124-000013984C1AC99E-9CC79721', 3, '5ml gorzka wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183127-000013992949B9E5-B2A427A0', '20081130183124-000013984C1AC99E-9CC79721', 4, '25ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183127-00001399295FE597-43AAE3BD', '20081130183124-000013984C1AC99E-9CC79721', 5, '25ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183127-000013992975D544-8D039EC6', '20081130183124-000013984C1AC99E-9CC79721', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183128-000013992990B6D0-04DAD529', '20081130183124-000013984C1AC99E-9CC79721', 7, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183141-0000139C3C26FBB1-944286A8', '20081130183140-0000139C007CC692-25E803EF', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183141-0000139C3C48B28C-D557B0D3', '20081130183140-0000139C007CC692-25E803EF', 2, '25ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183141-0000139C3C6A1331-CC4F0FB0', '20081130183140-0000139C007CC692-25E803EF', 3, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183141-0000139C3C8AD6B1-5DBC22FD', '20081130183140-0000139C007CC692-25E803EF', 4, '25ml sherry')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183141-0000139C3CA836A2-C4CD65E9', '20081130183140-0000139C007CC692-25E803EF', 5, '15ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183151-0000139EA9713F2A-5E9D9C30', '20081130183150-0000139E60B28631-F6DECF67', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183151-0000139EA98F0D53-2387C45A', '20081130183150-0000139E60B28631-F6DECF67', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183151-0000139EA9AB0DE1-3C9CAF8C', '20081130183150-0000139E60B28631-F6DECF67', 3, '25ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183151-0000139EA9C7F16A-B7409EF0', '20081130183150-0000139E60B28631-F6DECF67', 4, '25ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183151-0000139EA9E439FD-8CD180D2', '20081130183150-0000139E60B28631-F6DECF67', 5, 's³omka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183205-000013A1CA8242F3-687C2AB6', '20081130183159-000013A07A80938D-B2C32421', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183205-000013A1CAD22442-99A11723', '20081130183159-000013A07A80938D-B2C32421', 2, '75ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183205-000013A1CAF1885F-3D0451F5', '20081130183159-000013A07A80938D-B2C32421', 3, '30ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183205-000013A1CB0E3558-FB026C03', '20081130183159-000013A07A80938D-B2C32421', 4, '5ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183205-000013A1CB2A0184-BEBB6A2B', '20081130183159-000013A07A80938D-B2C32421', 5, '25ml sok ¿urawinowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183205-000013A1CB496C2D-0A061CAF', '20081130183159-000013A07A80938D-B2C32421', 6, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183205-000013A1CB67D77A-6043020C', '20081130183159-000013A07A80938D-B2C32421', 7, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183221-000013A5A9FB83A2-73B15269', '20081130183213-000013A3D0FF01A3-7FB006D5', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183221-000013A5AA1AC18D-E56CFF36', '20081130183213-000013A3D0FF01A3-7FB006D5', 2, '30ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183221-000013A5AA3FBB3A-1E532E4F', '20081130183213-000013A3D0FF01A3-7FB006D5', 3, '5ml Benedictine')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183221-000013A5AA5E9B4C-7A12EADB', '20081130183213-000013A3D0FF01A3-7FB006D5', 4, '20ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183221-000013A5AA7D898D-A9EEFEC1', '20081130183213-000013A3D0FF01A3-7FB006D5', 5, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183235-000013A8C5428997-DB4AD784', '20081130183231-000013A7E92E5A57-9355FD92', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183235-000013A8C560C083-4C6030F9', '20081130183231-000013A7E92E5A57-9355FD92', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183235-000013A8C57BDCFD-8FA34DA2', '20081130183231-000013A7E92E5A57-9355FD92', 3, '50ml Benedictine')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183235-000013A8C5960505-959859B6', '20081130183231-000013A7E92E5A57-9355FD92', 4, '50ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183235-000013A8C5B06E88-849881D3', '20081130183231-000013A7E92E5A57-9355FD92', 5, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183245-000013AB3BA50D15-E977DAAF', '20081130183243-000013AAA227E6DD-F2B1658E', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183245-000013AB3BC307FC-ADD1BD47', '20081130183243-000013AAA227E6DD-F2B1658E', 2, '30ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183245-000013AB3BDD2CBE-0BF101E1', '20081130183243-000013AAA227E6DD-F2B1658E', 3, '100ml cola')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183245-000013AB3BF70DD8-5FD54D27', '20081130183243-000013AAA227E6DD-F2B1658E', 4, '5ml angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183245-000013AB3C56A9F0-A4128ACA', '20081130183243-000013AAA227E6DD-F2B1658E', 5, 'mieszade³ko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183255-000013AD9BE53138-2F31C710', '20081130183254-000013AD64576531-B80A1C2C', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183255-000013AD9C03730E-753B3E4D', '20081130183254-000013AD64576531-B80A1C2C', 2, '40ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183255-000013AD9C1DC148-D0DDE74D', '20081130183254-000013AD64576531-B80A1C2C', 3, '15ml cukier puder')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183255-000013AD9C389E77-323BA3D6', '20081130183254-000013AD64576531-B80A1C2C', 4, '100ml ginger ale')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183255-000013AD9C529E1F-EB8AE86D', '20081130183254-000013AD64576531-B80A1C2C', 5, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183255-000013AD9CB18CB4-600553CE', '20081130183254-000013AD64576531-B80A1C2C', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183306-000013B0021D59A1-220ACF5C', '20081130183305-000013AFC4CC2D6C-DFB58243', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183306-000013B0023CA5BC-55A0AC0C', '20081130183305-000013AFC4CC2D6C-DFB58243', 2, '30ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183306-000013B0025806F6-AE167238', '20081130183305-000013AFC4CC2D6C-DFB58243', 3, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183306-000013B002AEF53A-F1A03E58', '20081130183305-000013AFC4CC2D6C-DFB58243', 4, '10ml angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183306-000013B002CACC50-C76191C7', '20081130183305-000013AFC4CC2D6C-DFB58243', 5, '5ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183306-000013B002E4DE85-5B365980', '20081130183305-000013AFC4CC2D6C-DFB58243', 6, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183315-000013B21EF9F1F7-34AC16F9', '20081130183313-000013B1D0D6C0D9-C2AFD34D', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183315-000013B21F1945B6-0A383B37', '20081130183313-000013B1D0D6C0D9-C2AFD34D', 2, '50ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183315-000013B21F33B167-F2652FAC', '20081130183313-000013B1D0D6C0D9-C2AFD34D', 3, '10ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183315-000013B21F4D521E-1F29DE0A', '20081130183313-000013B1D0D6C0D9-C2AFD34D', 4, '15ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183315-000013B21F697E52-CB03B2DF', '20081130183313-000013B1D0D6C0D9-C2AFD34D', 5, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183315-000013B21F862D7A-F0BBDF48', '20081130183313-000013B1D0D6C0D9-C2AFD34D', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183315-000013B21FA04981-461B865A', '20081130183313-000013B1D0D6C0D9-C2AFD34D', 7, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183315-000013B21FBA45E3-565CF2A4', '20081130183313-000013B1D0D6C0D9-C2AFD34D', 8, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183324-000013B44ECD17D7-D8BB344D', '20081130183323-000013B40C9792B4-41E26298', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183324-000013B44EEAD373-5FE10EC6', '20081130183323-000013B40C9792B4-41E26298', 2, '50ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183324-000013B44F0552C8-091D89F5', '20081130183323-000013B40C9792B4-41E26298', 3, '5ml gorzka wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183324-000013B44F20EDD9-A9332BD6', '20081130183323-000013B40C9792B4-41E26298', 4, '15ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183324-000013B44F3BD937-318BED99', '20081130183323-000013B40C9792B4-41E26298', 5, '15ml cukier puder')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183324-000013B44F55BFC6-9015A563', '20081130183323-000013B40C9792B4-41E26298', 6, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183336-000013B6FD25B7D3-5F541F7A', '20081130183333-000013B65C103ED1-710C238B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183336-000013B6FD43EC91-11086558', '20081130183333-000013B65C103ED1-710C238B', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183336-000013B6FD5E3BE2-32E1FC8B', '20081130183333-000013B65C103ED1-710C238B', 3, '50ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183336-000013B6FD7950B9-96A1DBFE', '20081130183333-000013B65C103ED1-710C238B', 4, '50ml angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183336-000013B6FDA3AEDA-BC18250A', '20081130183333-000013B65C103ED1-710C238B', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183336-000013B6FDC4E94E-946D1EEB', '20081130183333-000013B65C103ED1-710C238B', 6, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183344-000013B8F629CA2F-14699F0F', '20081130183343-000013B8BB481B7E-BCACD99C', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183344-000013B8F64A81AE-B819558D', '20081130183343-000013B8BB481B7E-BCACD99C', 2, '50ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183344-000013B8F665BB9F-79D4FAC2', '20081130183343-000013B8BB481B7E-BCACD99C', 3, '10ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183344-000013B8F67F6857-7132CC03', '20081130183343-000013B8BB481B7E-BCACD99C', 4, '5ml syrop migda³owy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183344-000013B8F69BA4EA-E93A52A2', '20081130183343-000013B8BB481B7E-BCACD99C', 5, '25ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183344-000013B8F6B8F47C-655F88D5', '20081130183343-000013B8BB481B7E-BCACD99C', 6, 'jedna malina')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183344-000013B8F6D5968C-FF6CD476', '20081130183343-000013B8BB481B7E-BCACD99C', 7, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183353-000013BAFFB4413C-D05964D6', '20081130183352-000013BABDC91835-4B1D9F89', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183353-000013BAFFD6158D-B103EA58', '20081130183352-000013BABDC91835-4B1D9F89', 2, '15ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183353-000013BAFFF3EA42-57A8099C', '20081130183352-000013BABDC91835-4B1D9F89', 3, '50ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183353-000013BB00464E53-D9667BB8', '20081130183352-000013BABDC91835-4B1D9F89', 4, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183402-000013BD290643F4-BCABFCDA', '20081130183401-000013BCE1AD442C-A1264726', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183402-000013BD292463F5-EC8347A6', '20081130183401-000013BCE1AD442C-A1264726', 2, '30ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183402-000013BD29780367-9C1E0899', '20081130183401-000013BCE1AD442C-A1264726', 3, '200ml mleko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183402-000013BD29928831-49C2E62D', '20081130183401-000013BCE1AD442C-A1264726', 4, '125ml t³usta œmietana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183402-000013BD29AC082B-45CD28B6', '20081130183401-000013BCE1AD442C-A1264726', 5, '15ml kakao (w proszku)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183402-000013BD29C55C7F-BDB5C760', '20081130183401-000013BCE1AD442C-A1264726', 6, '60g mleczna czekolada (w ma³ych kawa³kach)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183402-000013BD29DE8CCF-E73B1BCD', '20081130183401-000013BCE1AD442C-A1264726', 7, 'bita œmietana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183402-000013BD29FA755B-24DD26AD', '20081130183401-000013BCE1AD442C-A1264726', 8, 'tarta czekolada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183413-000013BFA317FB4C-7F72ABD0', '20081130183411-000013BF56A5938C-0E734806', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183413-000013BFA336A070-4C3CE973', '20081130183411-000013BF56A5938C-0E734806', 2, '25ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183413-000013BFA356CD58-45D5186F', '20081130183411-000013BF56A5938C-0E734806', 3, '10ml creme de banane')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183413-000013BFA371DCBA-0B144639', '20081130183411-000013BF56A5938C-0E734806', 4, '20ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183413-000013BFA38B9F45-177D40F0', '20081130183411-000013BF56A5938C-0E734806', 5, '20ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183413-000013BFA3A4CD67-84BA6AFD', '20081130183411-000013BF56A5938C-0E734806', 6, '25ml sok z marakui(w proszku)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183413-000013BFA3C25272-A28E1163', '20081130183411-000013BF56A5938C-0E734806', 7, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183413-000013BFA3DD6749-BE9E833F', '20081130183411-000013BF56A5938C-0E734806', 8, 'ananas')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183421-000013C19579D86E-183FA34D', '20081130183420-000013C14513D87E-252594D5', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183421-000013C195991770-747BE545', '20081130183420-000013C14513D87E-252594D5', 2, '20ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183421-000013C195B3B66B-F3DCEEBB', '20081130183420-000013C14513D87E-252594D5', 3, '25ml rum (ciemny)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183421-000013C195CD2FD9-CFF9D23B', '20081130183420-000013C14513D87E-252594D5', 4, '25ml œmietana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183421-000013C195E6A08C-E9AE3525', '20081130183420-000013C14513D87E-252594D5', 5, 'czekolada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183429-000013C36E0D0DE0-B43B38C8', '20081130183428-000013C321EA553C-7B7F200C', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183429-000013C36E2FC645-CB6A6F7B', '20081130183428-000013C321EA553C-7B7F200C', 2, '20ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183429-000013C36E8187BC-9B005BD9', '20081130183428-000013C321EA553C-7B7F200C', 3, '10ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183429-000013C36E9CCFDD-61C8A96D', '20081130183428-000013C321EA553C-7B7F200C', 4, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183429-000013C36EBB183F-AC517166', '20081130183428-000013C321EA553C-7B7F200C', 5, '25ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183429-000013C36ED5967D-780B53A3', '20081130183428-000013C321EA553C-7B7F200C', 6, 'Plasterek grejpfruta')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183438-000013C58979936A-EE56118B', '20081130183436-000013C51B6BCB4C-18C57B5B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183438-000013C58998DC3F-0C25E2B8', '20081130183436-000013C51B6BCB4C-18C57B5B', 2, '35ml szkoca whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183438-000013C589B5E829-DBB552D1', '20081130183436-000013C51B6BCB4C-18C57B5B', 3, '50ml angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183438-000013C589CFA657-A130F1BC', '20081130183436-000013C51B6BCB4C-18C57B5B', 4, '5ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183438-000013C589EF3849-7FB1D82B', '20081130183436-000013C51B6BCB4C-18C57B5B', 5, 'kawa³ek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183447-000013C797A7B8D5-882E3001', '20081130183446-000013C75059DDA2-346DF560', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183447-000013C797C5469C-CAE34E0C', '20081130183446-000013C75059DDA2-346DF560', 2, '35ml szkoca whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183447-000013C797DE6D1A-FC09A0CF', '20081130183446-000013C75059DDA2-346DF560', 3, '25ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183447-000013C797F802E7-85350BC7', '20081130183446-000013C75059DDA2-346DF560', 4, '25ml Pernod')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183455-000013C9969F1CE2-719C8776', '20081130183454-000013C958197FA8-C0021B78', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183455-000013C996EEFC02-06F1EB0A', '20081130183454-000013C958197FA8-C0021B78', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183455-000013C99709011F-2CFD6499', '20081130183454-000013C958197FA8-C0021B78', 3, '5ml wódka gorzka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183455-000013C99721DE80-9113B35D', '20081130183454-000013C958197FA8-C0021B78', 4, '25ml wermut rosso')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183455-000013C9973A5CF0-814F45D0', '20081130183454-000013C958197FA8-C0021B78', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183504-000013CB8CF9323B-06E2D52E', '20081130183503-000013CB4CDAD320-B584ED70', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183504-000013CB8D174BB0-516D7A63', '20081130183503-000013CB4CDAD320-B584ED70', 2, '30ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183504-000013CB8D305B44-0F3701B3', '20081130183503-000013CB4CDAD320-B584ED70', 3, '25ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183504-000013CB8D494D60-3DC86FE7', '20081130183503-000013CB4CDAD320-B584ED70', 4, '15ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183504-000013CB8D649352-12330E90', '20081130183503-000013CB4CDAD320-B584ED70', 5, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183504-000013CB8D8001A5-8879AF40', '20081130183503-000013CB4CDAD320-B584ED70', 6, 'bia³ko jajka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183513-000013CDA7BBCED7-EC610922', '20081130183511-000013CD46AF3D4E-82DD7AED', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183513-000013CDA7D9B502-693B6C0B', '20081130183511-000013CD46AF3D4E-82DD7AED', 2, '50ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183513-000013CDA7F27FD6-26639645', '20081130183511-000013CD46AF3D4E-82DD7AED', 3, '15ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183513-000013CDA80AF9E8-819AADBB', '20081130183511-000013CD46AF3D4E-82DD7AED', 4, '5ml gor¹ca woda')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183513-000013CDA82599FB-17E91194', '20081130183511-000013CD46AF3D4E-82DD7AED', 5, 'ok. 10 listków miêty')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183524-000013D03A17FB30-4271CFA5', '20081130183523-000013CFF9B9AC20-FED84B10', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183524-000013D03A32879E-EEEE0B7E', '20081130183523-000013CFF9B9AC20-FED84B10', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183524-000013D03A4BAAD6-E8F03D14', '20081130183523-000013CFF9B9AC20-FED84B10', 3, 'sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183524-000013D03A652216-F25A938D', '20081130183523-000013CFF9B9AC20-FED84B10', 4, '25ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183524-000013D03A7E1661-D4B0F775', '20081130183523-000013CFF9B9AC20-FED84B10', 5, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183524-000013D03A9874FA-74139BD6', '20081130183523-000013CFF9B9AC20-FED84B10', 6, 'skórka pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183534-000013D27624F266-DFA567F8', '20081130183532-000013D22F2EDC72-FB4F1069', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183534-000013D2764061D0-25628308', '20081130183532-000013D22F2EDC72-FB4F1069', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183534-000013D27659E856-1E1729B4', '20081130183532-000013D22F2EDC72-FB4F1069', 3, '5ml angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183534-000013D27672C271-0193441D', '20081130183532-000013D22F2EDC72-FB4F1069', 4, '5ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183534-000013D2768B3D9B-44DEFBB7', '20081130183532-000013D22F2EDC72-FB4F1069', 5, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183534-000013D276A3A2F1-ABF47030', '20081130183532-000013D22F2EDC72-FB4F1069', 6, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183534-000013D276BE787C-E812F187', '20081130183532-000013D22F2EDC72-FB4F1069', 7, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183543-000013D494378386-317340D0', '20081130183541-000013D44350E7F8-3C3A1581', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183543-000013D494556554-343C2CFC', '20081130183541-000013D44350E7F8-3C3A1581', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183543-000013D494816682-DB96B441', '20081130183541-000013D44350E7F8-3C3A1581', 3, '75ml angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183543-000013D4949E69B1-059382E3', '20081130183541-000013D44350E7F8-3C3A1581', 4, '15ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183543-000013D494BAB473-ED72A5C4', '20081130183541-000013D44350E7F8-3C3A1581', 5, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183552-000013D6B0F5436F-442C58D3', '20081130183550-000013D658743A01-E94D4375', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183552-000013D6B1136487-D4F62B90', '20081130183550-000013D658743A01-E94D4375', 2, '30ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183552-000013D6B12FFBAD-67FBB5EE', '20081130183550-000013D658743A01-E94D4375', 3, '5ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183552-000013D6B14B8202-22998A80', '20081130183550-000013D658743A01-E94D4375', 4, '15ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183552-000013D6B1668BEF-7A2DF88C', '20081130183550-000013D658743A01-E94D4375', 5, 'skórka pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183552-000013D6B1827C1E-E0952B56', '20081130183550-000013D658743A01-E94D4375', 6, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183600-000013D88CD89168-06E95535', '20081130183559-000013D84CA49B05-06012F91', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183600-000013D88CF8B8DB-0A980D1B', '20081130183559-000013D84CA49B05-06012F91', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183600-000013D88D13C60E-EAECCA0A', '20081130183559-000013D84CA49B05-06012F91', 3, '25ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183600-000013D88D2FC355-CDD38945', '20081130183559-000013D84CA49B05-06012F91', 4, '25ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183600-000013D88D4B87DE-CB3CCCB0', '20081130183559-000013D84CA49B05-06012F91', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183609-000013DAC365A8B2-1C8F8CD9', '20081130183608-000013DA7F469162-85468974', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183609-000013DAC384834F-6DB972E9', '20081130183608-000013DA7F469162-85468974', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183609-000013DAC39EEBBA-84452A20', '20081130183608-000013DA7F469162-85468974', 3, '10ml ¿ó³ty likier Chartreuse')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183609-000013DAC3EBFDE3-14406621', '20081130183608-000013DA7F469162-85468974', 4, '10ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183609-000013DAC405CE9E-CC7FDF09', '20081130183608-000013DA7F469162-85468974', 5, 'kawa³ek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183617-000013DC8AC3DDF0-21B5F233', '20081130183616-000013DC5583499D-DFEB77F9', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183617-000013DC8AE119DE-9960F4A1', '20081130183616-000013DC5583499D-DFEB77F9', 2, '30ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183617-000013DC8AF9A67E-7991764E', '20081130183616-000013DC5583499D-DFEB77F9', 3, '5ml gazowany napój brzoskwiniowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183617-000013DC8B11919B-5D70EA8A', '20081130183616-000013DC5583499D-DFEB77F9', 4, '15ml creme de mure')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183617-000013DC8B2928B2-59672B65', '20081130183616-000013DC5583499D-DFEB77F9', 5, 'kawa³ek brzoskwini')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183629-000013DF5440FDD3-B4147800', '20081130183624-000013DE2666FDF1-3101DCD5', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183629-000013DF545B4469-6F8E42BD', '20081130183624-000013DE2666FDF1-3101DCD5', 2, '30ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183629-000013DF54734ACF-F6C4CD2F', '20081130183624-000013DE2666FDF1-3101DCD5', 3, '10ml Kahlua')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183629-000013DF548B3EA8-4409D1BB', '20081130183624-000013DE2666FDF1-3101DCD5', 4, '25ml Bailey''s')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183629-000013DF54A2E505-7185A8ED', '20081130183624-000013DE2666FDF1-3101DCD5', 5, 'tarta czekolada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183638-000013E15D236F60-36698965', '20081130183637-000013E11C0AF430-0FBE76B8', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183638-000013E15D3DFCE6-F893337A', '20081130183637-000013E11C0AF430-0FBE76B8', 2, '25ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183638-000013E15D8C6C44-B1B90A89', '20081130183637-000013E11C0AF430-0FBE76B8', 3, '25ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183638-000013E15DBCFF49-1880B36D', '20081130183637-000013E11C0AF430-0FBE76B8', 4, '5ml angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183646-000013E354D3288E-E4350D1E', '20081130183645-000013E314A1F20A-DE2B4588', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183646-000013E354F0F489-15E619E1', '20081130183645-000013E314A1F20A-DE2B4588', 2, '30ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183646-000013E35508FE34-337551C4', '20081130183645-000013E314A1F20A-DE2B4588', 3, '10ml likier Glayva')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183655-000013E55245B982-AE6A8FCD', '20081130183654-000013E51CD6870E-B5968D78', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183655-000013E55260830C-634BDED2', '20081130183654-000013E51CD6870E-B5968D78', 2, '30ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183655-000013E552786D12-B06E857F', '20081130183654-000013E51CD6870E-B5968D78', 3, '35ml likier Drambuie')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183704-000013E76BE74602-D00E97B6', '20081130183702-000013E7199A2649-B8A0EA95', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183704-000013E76C37B98C-FBAD6567', '20081130183702-000013E7199A2649-B8A0EA95', 2, '30ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183704-000013E76C51E709-03A5ABDE', '20081130183702-000013E7199A2649-B8A0EA95', 3, '50ml angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183704-000013E76C6A07A0-89A385EE', '20081130183702-000013E7199A2649-B8A0EA95', 4, '25ml Benedictine')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183704-000013E76C81A65A-3B9FF10C', '20081130183702-000013E7199A2649-B8A0EA95', 5, '50ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183704-000013E76C99C4C2-E3397BE6', '20081130183702-000013E7199A2649-B8A0EA95', 6, 'kawa³ki pomarañczy (2 z¹bki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183704-000013E76CB12CEB-B36F2B96', '20081130183702-000013E7199A2649-B8A0EA95', 7, 'cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183704-000013E76CC8C519-28F59F56', '20081130183702-000013E7199A2649-B8A0EA95', 8, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183712-000013E96D817847-AC7994CE', '20081130183711-000013E9280ADE11-996E046D', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183712-000013E96D9BD5C9-6EDD41F9', '20081130183711-000013E9280ADE11-996E046D', 2, '75ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183712-000013E96DB38710-705FB8D7', '20081130183711-000013E9280ADE11-996E046D', 3, '25ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183712-000013E96DCAF397-4E710D71', '20081130183711-000013E9280ADE11-996E046D', 4, '25ml porto')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183712-000013E96DE23C1B-9D371D5D', '20081130183711-000013E9280ADE11-996E046D', 5, '5ml gorzka wódka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183720-000013EB4F8BAF95-E26F77CE', '20081130183719-000013EB01000259-B95EA348', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183720-000013EB4FA18EE4-6014CD0A', '20081130183719-000013EB01000259-B95EA348', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183720-000013EB4FB57EC2-461804C6', '20081130183719-000013EB01000259-B95EA348', 3, '10ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183720-000013EB4FC87801-A7F9C5E3', '20081130183719-000013EB01000259-B95EA348', 4, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183728-000013ED0826BEB3-A3058BE5', '20081130183727-000013ECC706827E-7AE17D15', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183728-000013ED0843F52C-1AB64747', '20081130183727-000013ECC706827E-7AE17D15', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183728-000013ED085C6684-E715844E', '20081130183727-000013ECC706827E-7AE17D15', 3, '10ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183728-000013ED0873F199-301669DA', '20081130183727-000013ECC706827E-7AE17D15', 4, '10ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183736-000013EEEB4FEF64-13C04977', '20081130183735-000013EEB3FDCCC7-29349E4F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183736-000013EEEB6C9B46-755F5A21', '20081130183735-000013EEB3FDCCC7-29349E4F', 2, '25ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183736-000013EEEB854BE8-2C0FE9E4', '20081130183735-000013EEB3FDCCC7-29349E4F', 3, '25ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183736-000013EEEB9ED26F-E5B7C82B', '20081130183735-000013EEB3FDCCC7-29349E4F', 4, 'kawa³ek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183743-000013F0ACB8994C-3BB47F75', '20081130183742-000013F0725DEC37-F5612CF1', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183743-000013F0ACD5CB68-FFD07F54', '20081130183742-000013F0725DEC37-F5612CF1', 2, '30ml bourbon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183743-000013F0ACEDDFFD-ECCEC9B9', '20081130183742-000013F0725DEC37-F5612CF1', 3, '25ml Campari')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183743-000013F0AD04F995-6BEDE4F1', '20081130183742-000013F0725DEC37-F5612CF1', 4, '15ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183743-000013F0AD1C62D6-1A31104D', '20081130183742-000013F0725DEC37-F5612CF1', 5, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183751-000013F27EAD04B7-58C043CA', '20081130183750-000013F24A79120B-72C98D2D', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183751-000013F27ECA3E76-6E375EC8', '20081130183750-000013F24A79120B-72C98D2D', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183751-000013F27EE214D8-E7172586', '20081130183750-000013F24A79120B-72C98D2D', 3, '20ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183751-000013F27F2F641E-73168997', '20081130183750-000013F24A79120B-72C98D2D', 4, '25ml Campari')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183751-000013F27F4ABDB4-ACDC2EE2', '20081130183750-000013F24A79120B-72C98D2D', 5, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183800-000013F47DA2CA15-C582A0EF', '20081130183758-000013F42468DBE8-5CCA9501', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183800-000013F47DC06C97-130A871D', '20081130183758-000013F42468DBE8-5CCA9501', 2, '40ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183800-000013F47DD86EA0-6D2B3EC8', '20081130183758-000013F42468DBE8-5CCA9501', 3, '25ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183800-000013F47DF065BE-DAD4BDAC', '20081130183758-000013F42468DBE8-5CCA9501', 4, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183808-000013F64F974924-1DE836C9', '20081130183806-000013F60CAD1229-7BE0F688', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183808-000013F64FB4656D-B24DBF96', '20081130183806-000013F60CAD1229-7BE0F688', 2, '40ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183808-000013F64FCC0AB3-D9EE5AEA', '20081130183806-000013F60CAD1229-7BE0F688', 3, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183808-000013F6501B916F-A185EF24', '20081130183806-000013F60CAD1229-7BE0F688', 4, '25ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183808-000013F65035C461-E9E9D927', '20081130183806-000013F60CAD1229-7BE0F688', 5, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183816-000013F82D7671BF-8D0277FB', '20081130183814-000013F7E1713225-79D75125', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183816-000013F82D93F8F9-455084D6', '20081130183814-000013F7E1713225-79D75125', 2, '40ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183816-000013F82DAB9E3F-D20FBE7A', '20081130183814-000013F7E1713225-79D75125', 3, '15ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183816-000013F82DC2F3DC-13E4895C', '20081130183814-000013F7E1713225-79D75125', 4, '15ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183816-000013F82DDAFFB7-AF30404E', '20081130183814-000013F7E1713225-79D75125', 5, '10ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183816-000013F82DF4E0D0-5B3FE230', '20081130183814-000013F7E1713225-79D75125', 6, 'woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183816-000013F82E0E2A3A-B99AD702', '20081130183814-000013F7E1713225-79D75125', 7, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183816-000013F82E2602CB-A9E17C77', '20081130183814-000013F7E1713225-79D75125', 8, 'pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183824-000013FA0C0FDF93-6CF8E583', '20081130183823-000013F9D5A3D35A-8BE6155F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183824-000013FA0C2D13DD-102CEC7D', '20081130183823-000013F9D5A3D35A-8BE6155F', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183824-000013FA0C4521E7-6CE05056', '20081130183823-000013F9D5A3D35A-8BE6155F', 3, '50ml wino imbirowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183833-000013FC46D15089-2F71DCD7', '20081130183832-000013FC02104A0F-D1FF2737', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183833-000013FC46EB6C90-E321863A', '20081130183832-000013FC02104A0F-D1FF2737', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183833-000013FC470334C2-9541AF8A', '20081130183832-000013FC02104A0F-D1FF2737', 3, '15ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183833-000013FC471A988E-80F1589C', '20081130183832-000013FC02104A0F-D1FF2737', 4, '50ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183833-000013FC47318451-E19C19E6', '20081130183832-000013FC02104A0F-D1FF2737', 5, 'brzoskwinia')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183833-000013FC4748B18D-A33E863B', '20081130183832-000013FC02104A0F-D1FF2737', 6, '5ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183843-000013FE7793E07F-62AD2190', '20081130183841-000013FE2E03ECB7-3F27425C', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183843-000013FE77ADE36D-52D6D48E', '20081130183841-000013FE2E03ECB7-3F27425C', 2, '50ml whisky')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183843-000013FE77C5833E-911DFF94', '20081130183841-000013FE2E03ECB7-3F27425C', 3, '15ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183843-000013FE77DC84D4-66024219', '20081130183841-000013FE2E03ECB7-3F27425C', 4, '50ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183843-000013FE77F3C157-3A231191', '20081130183841-000013FE2E03ECB7-3F27425C', 5, 'brzoskwinia')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130183843-000013FE780AD579-AA8815B2', '20081130183841-000013FE2E03ECB7-3F27425C', 6, '5ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184101-0000141EC27FEA6D-B52CC53B', '20081130184100-0000141E7815EC30-0B9ABF02', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184101-0000141EC29EDD0C-C580D298', '20081130184100-0000141E7815EC30-0B9ABF02', 2, '30ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184101-0000141EC2C52D62-A529E473', '20081130184100-0000141E7815EC30-0B9ABF02', 3, '25ml sok z mango')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184101-0000141EC2DF5E25-8E124591', '20081130184100-0000141E7815EC30-0B9ABF02', 4, '50ml Malibu')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184101-0000141EC2F8A78F-BAAE8F85', '20081130184100-0000141E7815EC30-0B9ABF02', 5, '25ml sok z brzoskwini')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184101-0000141EC310C082-D0D34013', '20081130184100-0000141E7815EC30-0B9ABF02', 6, '50ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184101-0000141EC327E61A-543CF788', '20081130184100-0000141E7815EC30-0B9ABF02', 7, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184112-000014213D38AD5E-1F104639', '20081130184111-00001420F59FF549-4F508B60', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184112-000014213D5309C8-3E6D86E0', '20081130184111-00001420F59FF549-4F508B60', 2, '30ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184112-000014213D6AA0DF-CC4AEB2B', '20081130184111-00001420F59FF549-4F508B60', 3, '25ml delikatny likier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184112-000014213D81EDC0-964A76E9', '20081130184111-00001420F59FF549-4F508B60', 4, '5ml orzech kokosowy w proszku')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184112-000014213D98FA3F-6E3DC65B', '20081130184111-00001420F59FF549-4F508B60', 5, '30ml œmietana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184112-000014213DAFF9A6-5225A57C', '20081130184111-00001420F59FF549-4F508B60', 6, 'czekolada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184120-0000142301B12773-8C09DE36', '20081130184119-00001422C684065F-41FCE088', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184120-0000142301CA1990-F74D7B96', '20081130184119-00001422C684065F-41FCE088', 2, '30ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184120-0000142301E1E61F-8DFB6358', '20081130184119-00001422C684065F-41FCE088', 3, '5ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184120-0000142301F8EF59-1E1DB71F', '20081130184119-00001422C684065F-41FCE088', 4, '10ml Creme de Banane')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184120-0000142302104724-370229DA', '20081130184119-00001422C684065F-41FCE088', 5, '75ml lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184120-0000142302272E89-464C69D4', '20081130184119-00001422C684065F-41FCE088', 6, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184128-00001424F6F98F15-36271997', '20081130184127-00001424B9BF6130-67CFE577', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184128-00001424F7172C23-04100A1E', '20081130184127-00001424B9BF6130-67CFE577', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184128-00001424F72F5D18-9BE6C65C', '20081130184127-00001424B9BF6130-67CFE577', 3, '100 ml ginger ale')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184128-00001424F7474607-83B42173', '20081130184127-00001424B9BF6130-67CFE577', 4, '15ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184128-00001424F75F0695-1A2DAA9A', '20081130184127-00001424B9BF6130-67CFE577', 5, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184128-00001424F776E7E0-E7137B79', '20081130184127-00001424B9BF6130-67CFE577', 6, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184136-00001426CD8F47F1-422D4413', '20081130184135-000014268A317A15-FE121063', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184136-00001426CDA9A572-A32AE35C', '20081130184135-000014268A317A15-FE121063', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184136-00001426CDC508DB-DF124836', '20081130184135-000014268A317A15-FE121063', 3, '100 g grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184136-00001426CDF091FF-49AE9E52', '20081130184135-000014268A317A15-FE121063', 4, 'sok z limy (pó³ owocu)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184136-00001426CE0B738B-9FDE882F', '20081130184135-000014268A317A15-FE121063', 5, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184146-00001429445A48A4-819FF3ED', '20081130184145-0000142903F03E67-A2D152B5', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184146-0000142944743161-EF9D2023', '20081130184145-0000142903F03E67-A2D152B5', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184146-00001429448CA08A-54C20BB5', '20081130184145-0000142903F03E67-A2D152B5', 3, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184146-0000142944A40F40-DCB75816', '20081130184145-0000142903F03E67-A2D152B5', 4, '100 g ananas (kawa³ki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184154-0000142B1B9542C3-F62EC4BC', '20081130184153-0000142AD91DAE36-0883AA27', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184154-0000142B1BAF84FC-389E364B', '20081130184153-0000142AD91DAE36-0883AA27', 2, '30ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184154-0000142B1BC779EC-A2404AC7', '20081130184153-0000142AD91DAE36-0883AA27', 3, '25ml sok brzoskwioniowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184154-0000142B1BDF421E-6FB12F45', '20081130184153-0000142AD91DAE36-0883AA27', 4, '15ml peach brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184154-0000142B1BF6D706-226BF10D', '20081130184153-0000142AD91DAE36-0883AA27', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184154-0000142B1C0EAF96-2857C8EA', '20081130184153-0000142AD91DAE36-0883AA27', 6, 'kawa³ek brzoskwini')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184203-0000142D2474FAFE-9715C8C3', '20081130184202-0000142CDA2D0D18-16E2FDAF', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184203-0000142D24920D74-BF84ED7A', '20081130184202-0000142CDA2D0D18-16E2FDAF', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184203-0000142D24A9BEBC-D5BD7D14', '20081130184202-0000142CDA2D0D18-16E2FDAF', 3, '25ml krem kokosowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184203-0000142D24C14574-A1BC9C61', '20081130184202-0000142CDA2D0D18-16E2FDAF', 4, '25ml niebieski Curacao')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184203-0000142D24DC55ED-1C938B28', '20081130184202-0000142CDA2D0D18-16E2FDAF', 5, '50ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184203-0000142D24F4F0BC-9713BB5D', '20081130184202-0000142CDA2D0D18-16E2FDAF', 6, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184203-0000142D250C04DF-E7EBAD92', '20081130184202-0000142CDA2D0D18-16E2FDAF', 7, 'kawa³ek brzoskwini')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184203-0000142D2523CBFA-CEA0B5FB', '20081130184202-0000142CDA2D0D18-16E2FDAF', 8, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184203-0000142D35A92208-C994B6C0', '20081130184202-0000142CDA2D0D18-16E2FDAF', 9, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184203-0000142D35BE00E4-1FB00661', '20081130184202-0000142CDA2D0D18-16E2FDAF', 10, 'ananas')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184213-0000142F7AC810BB-E87402A6', '20081130184210-0000142ECD7A985C-FCB9BCCF', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184213-0000142F7AE2191E-6DC16D0D', '20081130184210-0000142ECD7A985C-FCB9BCCF', 2, '30ml z³oty rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184213-0000142F7AFA632C-5050BA53', '20081130184210-0000142ECD7A985C-FCB9BCCF', 3, '5ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184213-0000142F7B14EB3D-029D3C72', '20081130184210-0000142ECD7A985C-FCB9BCCF', 4, '15ml calvados')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184213-0000142F7B2D1148-2E31F347', '20081130184210-0000142ECD7A985C-FCB9BCCF', 5, 'cytryna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184221-0000143167365F0F-ED0AA357', '20081130184220-000014312B006AD5-9BF72F21', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184221-0000143167537D86-19B5B225', '20081130184220-000014312B006AD5-9BF72F21', 2, '60ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184221-00001431676BA27A-68E0812B', '20081130184220-000014312B006AD5-9BF72F21', 3, '10ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184221-000014316782F2A2-DBE44EAB', '20081130184220-000014312B006AD5-9BF72F21', 4, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184221-00001431679A6B2A-CE53A23D', '20081130184220-000014312B006AD5-9BF72F21', 5, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184229-0000143333C9700D-2DF4021C', '20081130184228-00001432FBBCA82F-912722A9', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184229-0000143333E3BD30-B548C4D8', '20081130184228-00001432FBBCA82F-912722A9', 2, '30ml z³oty rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184229-0000143333FB6D60-7D12CFEA', '20081130184228-00001432FBBCA82F-912722A9', 3, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184229-000014333412D35B-916BDDC1', '20081130184228-00001432FBBCA82F-912722A9', 4, '100 g melon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184229-000014333429C7D8-E943888A', '20081130184228-00001432FBBCA82F-912722A9', 5, '5ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184229-000014333441345F-7C5C5727', '20081130184228-00001432FBBCA82F-912722A9', 6, '10ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184229-00001433345827C5-86AF86CF', '20081130184228-00001432FBBCA82F-912722A9', 7, 'plasterek melona')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184238-000014352F879B54-FD53F666', '20081130184236-00001434DF72B92A-95ADDC9E', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184238-000014352FA5D46E-2F8F45C6', '20081130184236-00001434DF72B92A-95ADDC9E', 2, '25ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184238-000014352FC1577D-C0B08AD3', '20081130184236-00001434DF72B92A-95ADDC9E', 3, '10ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184238-000014352FD9F9F0-D9BC4F0B', '20081130184236-00001434DF72B92A-95ADDC9E', 4, '25ml porto')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184238-000014352FF147E9-D73113CB', '20081130184236-00001434DF72B92A-95ADDC9E', 5, '25ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184238-000014353008CFB8-DC278471', '20081130184236-00001434DF72B92A-95ADDC9E', 6, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184248-000014377E91FF33-10465393', '20081130184247-00001437443259B2-DB58DF4B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184248-000014377EAC38B1-9F886FBF', '20081130184247-00001437443259B2-DB58DF4B', 2, '30ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184248-000014377EC53C43-E4741F9F', '20081130184247-00001437443259B2-DB58DF4B', 3, '50ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184248-000014377F15AEB6-DD19F07E', '20081130184247-00001437443259B2-DB58DF4B', 4, '50ml sok ¿urawinowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184248-000014377F2E140C-6D2388A3', '20081130184247-00001437443259B2-DB58DF4B', 5, '10ml Creme de Banane')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184248-000014377F47AAF1-89D6B57A', '20081130184247-00001437443259B2-DB58DF4B', 6, '5ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184248-000014377F61BC0F-DF5A6AFF', '20081130184247-00001437443259B2-DB58DF4B', 7, 'ananas')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184248-000014377F7C9083-5103623C', '20081130184247-00001437443259B2-DB58DF4B', 8, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184256-00001439818158E5-0E0734E7', '20081130184255-00001439472FF154-B9886C7A', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184256-00001439819EC191-FF026576', '20081130184255-00001439472FF154-B9886C7A', 2, '30ml z³oty rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184256-0000143981BCA8D4-5EBAE86B', '20081130184255-00001439472FF154-B9886C7A', 3, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184256-0000143981D5911E-9FC1B3AD', '20081130184255-00001439472FF154-B9886C7A', 4, '10ml creme de menthe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184256-0000143981ED5095-F70A46DB', '20081130184255-00001439472FF154-B9886C7A', 5, 'listek miêty')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184304-0000143B3AF70285-A950B006', '20081130184303-0000143B03B856A5-EE003A25', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184304-0000143B3B1151D6-4AA93E16', '20081130184303-0000143B03B856A5-EE003A25', 2, '25ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184304-0000143B3B2BB4CC-C2B5E293', '20081130184303-0000143B03B856A5-EE003A25', 3, '10ml Creme de Cacao')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184304-0000143B3B47520B-C1774646', '20081130184303-0000143B03B856A5-EE003A25', 4, '20ml Malibu')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184304-0000143B3B5FC7C0-B6625291', '20081130184303-0000143B03B856A5-EE003A25', 5, '30ml sok mandarynkowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184304-0000143B3B76D440-B30BC8B4', '20081130184303-0000143B03B856A5-EE003A25', 6, 'mandarynka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184314-0000143DB0681C1E-2720751E', '20081130184310-0000143CB769F3AB-6E47B296', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184314-0000143DB0851E36-89EC7D60', '20081130184310-0000143CB769F3AB-6E47B296', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184314-0000143DB09CC493-A1518333', '20081130184310-0000143CB769F3AB-6E47B296', 3, '125ml Coca Cola')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184314-0000143DB0B36621-74AD843C', '20081130184310-0000143CB769F3AB-6E47B296', 4, 'sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184314-0000143DB0CAC9ED-3F91A7EF', '20081130184310-0000143CB769F3AB-6E47B296', 5, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184323-0000143FAA12EDAF-096BD14B', '20081130184322-0000143F6A50825D-D1F0CB47', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184323-0000143FAA2ED63A-D3229EF3', '20081130184322-0000143F6A50825D-D1F0CB47', 2, '30ml z³oty rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184323-0000143FAA4667DC-88023096', '20081130184322-0000143F6A50825D-D1F0CB47', 3, '25ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184323-0000143FAA5D5A2A-9388C223', '20081130184322-0000143F6A50825D-D1F0CB47', 4, '15ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184323-0000143FAA758036-6BFE2297', '20081130184322-0000143F6A50825D-D1F0CB47', 5, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184331-00001441A9B2B1CE-F6182381', '20081130184329-00001441431A5986-C9572F84', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184331-00001441A9D0AEE4-E495EA10', '20081130184329-00001441431A5986-C9572F84', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184331-00001441A9E8A08E-083FB5EC', '20081130184329-00001441431A5986-C9572F84', 3, '25ml s³odki wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184331-00001441AA00105B-7E89CB4B', '20081130184329-00001441431A5986-C9572F84', 4, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184331-00001441AA177656-997FBC33', '20081130184329-00001441431A5986-C9572F84', 5, '75ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184331-00001441AA2E7048-6165DDA3', '20081130184329-00001441431A5986-C9572F84', 6, '5ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184331-00001441AA456FAF-03442D2C', '20081130184329-00001441431A5986-C9572F84', 7, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184343-00001444752BAE82-679CF389', '20081130184342-000014442ED1295C-1742452E', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184343-000014447545E28B-BBF7522B', '20081130184342-000014442ED1295C-1742452E', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184343-00001444755D6FCF-358434F5', '20081130184342-000014442ED1295C-1742452E', 3, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184343-000014447574CE27-103D2B28', '20081130184342-000014442ED1295C-1742452E', 4, '25ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184343-00001444758BC3BB-3AED0F15', '20081130184342-000014442ED1295C-1742452E', 5, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184351-0000144631F1F911-17E533BF', '20081130184349-00001445E6AB5254-768D1DD5', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184351-00001446320C59D8-53397902', '20081130184349-00001445E6AB5254-768D1DD5', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184351-000014463223C548-B6CBD6CA', '20081130184349-00001445E6AB5254-768D1DD5', 3, '15ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184351-00001446323E6672-3E3BA78B', '20081130184349-00001445E6AB5254-768D1DD5', 4, '15ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184351-0000144632561D2E-892CBE74', '20081130184349-00001445E6AB5254-768D1DD5', 5, '5ml wódka gorzka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184351-00001446326D2438-9735338F', '20081130184349-00001445E6AB5254-768D1DD5', 6, 'skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184358-00001447FB4BB600-A5ED6731', '20081130184357-00001447C3D094A6-E5809CB0', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184358-00001447FB6886FD-FD8AA657', '20081130184357-00001447C3D094A6-E5809CB0', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184358-00001447FB80DD0C-548061E8', '20081130184357-00001447C3D094A6-E5809CB0', 3, '15ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184358-00001447FB9930ED-D8D78F67', '20081130184357-00001447C3D094A6-E5809CB0', 4, '25ml wermut rosso')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184358-00001447FBB2E777-8794314C', '20081130184357-00001447C3D094A6-E5809CB0', 5, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184358-00001447FBCC1E54-11EB15D0', '20081130184357-00001447C3D094A6-E5809CB0', 6, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184358-00001447FBE359C0-CDB4EEF6', '20081130184357-00001447C3D094A6-E5809CB0', 7, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184406-00001449B21F085A-D9FF5F20', '20081130184405-0000144972A44202-934E73C2', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184406-00001449B23BF49E-FAAC4473', '20081130184405-0000144972A44202-934E73C2', 2, '25ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184406-00001449B253B52D-D9CDD353', '20081130184405-0000144972A44202-934E73C2', 3, '15ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184406-00001449B26AFC9A-9FFFFF14', '20081130184405-0000144972A44202-934E73C2', 4, '25ml sok z marakui')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184406-00001449B2817C53-3795A536', '20081130184405-0000144972A44202-934E73C2', 5, '15ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184414-0000144BAE6CBBC8-CDD034BD', '20081130184413-0000144B7591EDBE-9C6481A0', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184414-0000144BAE897E94-6A9C7944', '20081130184413-0000144B7591EDBE-9C6481A0', 2, '25ml z³oty rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184414-0000144BAED60855-70328B47', '20081130184413-0000144B7591EDBE-9C6481A0', 3, '15ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184414-0000144BAEEEC1B3-7B4E1D6D', '20081130184413-0000144B7591EDBE-9C6481A0', 4, '25ml Malibu')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184414-0000144BAF05F34C-F97D8B6D', '20081130184413-0000144B7591EDBE-9C6481A0', 5, '25ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184414-0000144BAF1D5947-439A9C7B', '20081130184413-0000144B7591EDBE-9C6481A0', 6, '25ml sok z ¿urawiny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184414-0000144BAF348683-5FCC13AC', '20081130184413-0000144B7591EDBE-9C6481A0', 7, '25ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184414-0000144BAF4C8546-31D5F686', '20081130184413-0000144B7591EDBE-9C6481A0', 8, '25ml sok z pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184414-0000144BAF639CAE-EF5A164E', '20081130184413-0000144B7591EDBE-9C6481A0', 9, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184422-0000144D8F9D92DD-9F865C5B', '20081130184421-0000144D40B7FEBE-5B7323EF', 1, 'Lód kruszonym')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184422-0000144D8FB8BD87-409C3D11', '20081130184421-0000144D40B7FEBE-5B7323EF', 2, '30ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184422-0000144D8FD08B2E-2847E9CF', '20081130184421-0000144D40B7FEBE-5B7323EF', 3, '60ml lody kokosowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184422-0000144D8FF4BE3F-BBAA9679', '20081130184421-0000144D40B7FEBE-5B7323EF', 4, '25ml Malibu')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184422-0000144D900D0CAA-BD2C766F', '20081130184421-0000144D40B7FEBE-5B7323EF', 5, 'wiórki kokosowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184430-0000144F728CE7C0-E130BABB', '20081130184429-0000144F2BED4C64-E569A560', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184430-0000144F72A613B3-676AD6B7', '20081130184429-0000144F2BED4C64-E569A560', 2, '25ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184430-0000144F72BDCB86-1ACBA6F2', '20081130184429-0000144F2BED4C64-E569A560', 3, '50ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184430-0000144F72D52E3B-59B852CF', '20081130184429-0000144F2BED4C64-E569A560', 4, '75ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184430-0000144F72EC30E8-F228717E', '20081130184429-0000144F2BED4C64-E569A560', 5, '25ml Malibu')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184430-0000144F7303244E-BCFF9D67', '20081130184429-0000144F2BED4C64-E569A560', 6, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184430-0000144F731A56FF-44C3EF74', '20081130184429-0000144F2BED4C64-E569A560', 7, 'kawa³ek ananasa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184438-000014514A054428-E0A3241E', '20081130184437-00001451092613A3-61F024D8', 1, 'Lód kruszony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184438-000014514A2287B9-07EB17C6', '20081130184437-00001451092613A3-61F024D8', 2, '25ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184438-000014514A402C6B-C0F9D083', '20081130184437-00001451092613A3-61F024D8', 3, '50ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184438-000014514A584446-EA7BD360', '20081130184437-00001451092613A3-61F024D8', 4, '75ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184438-000014514A6F2407-A77B6C89', '20081130184437-00001451092613A3-61F024D8', 5, '25ml Malibu')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184438-000014514A8B8D58-8EC3B7E7', '20081130184437-00001451092613A3-61F024D8', 6, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184438-000014514AA3D309-3724168B', '20081130184437-00001451092613A3-61F024D8', 7, 'kawa³ek ananasa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184446-00001452F6E7B76B-3782A00D', '20081130184444-00001452A43FB5D2-5A420486', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184446-00001452F7064D48-85E335F7', '20081130184444-00001452A43FB5D2-5A420486', 2, '75ml z³oty rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184446-00001452F71DD518-419BBB60', '20081130184444-00001452A43FB5D2-5A420486', 3, '5ml creme de banane')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184446-00001452F736CA7A-7566E459', '20081130184444-00001452A43FB5D2-5A420486', 4, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184446-00001452F74DCD27-82FBDA79', '20081130184444-00001452A43FB5D2-5A420486', 5, 'banan')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184446-00001452F764A316-74E4B5AD', '20081130184444-00001452A43FB5D2-5A420486', 6, 'ga³ka muszkatowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184453-00001454D1773388-5EA72E86', '20081130184453-0000145499674309-2CDF015C', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184453-00001454D19391EF-66FC92A8', '20081130184453-0000145499674309-2CDF015C', 2, '25ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184453-00001454D1ABBE87-96E5A12D', '20081130184453-0000145499674309-2CDF015C', 3, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184453-00001454D1C2B761-CBF005A5', '20081130184453-0000145499674309-2CDF015C', 4, '50ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184453-00001454D1DA9E21-1EBA75B0', '20081130184453-0000145499674309-2CDF015C', 5, 'lima')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184453-00001454D22667D2-ED9275D1', '20081130184453-0000145499674309-2CDF015C', 6, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184453-00001454D23F2247-FFD59AAB', '20081130184453-0000145499674309-2CDF015C', 7, '100ml ginger ale')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184502-00001456B0A887AD-3F743C6F', '20081130184501-0000145685123475-6DF5E07D', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184502-00001456B0C5ACB0-4F810671', '20081130184501-0000145685123475-6DF5E07D', 2, '50ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184502-00001456B0DE02BF-F294DF6B', '20081130184501-0000145685123475-6DF5E07D', 3, '5ml cynamon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184502-00001456B0F52A87-C50DD447', '20081130184501-0000145685123475-6DF5E07D', 4, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184502-00001456B10C2D34-21C17454', '20081130184501-0000145685123475-6DF5E07D', 5, '20g mas³o')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184502-00001456B122A549-6B1ED44E', '20081130184501-0000145685123475-6DF5E07D', 6, 'gor¹ca woda')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184509-000014588A72B772-B0567074', '20081130184509-0000145855776B89-BD3EDF1B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184509-000014588A8C1369-1FED1C50', '20081130184509-0000145855776B89-BD3EDF1B', 2, '30ml z³oty rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184509-000014588AA372D8-17529CF4', '20081130184509-0000145855776B89-BD3EDF1B', 3, '5ml cynamon')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184509-000014588ABA6CCA-01E7AA1A', '20081130184509-0000145855776B89-BD3EDF1B', 4, '5ml miód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184509-000014588AD12888-17894916', '20081130184509-0000145855776B89-BD3EDF1B', 5, '200ml herbata')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184509-000014588AEA58D9-963A4E13', '20081130184509-0000145855776B89-BD3EDF1B', 6, 'imbir')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184509-000014588B02616D-1252B1C8', '20081130184509-0000145855776B89-BD3EDF1B', 7, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8D7C558D-8B87A919', '20081130184517-0000145A4C812E4D-66CE94C4', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8D993A2E-D71767A9', '20081130184517-0000145A4C812E4D-66CE94C4', 2, '25ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8DB0E82F-3119A250', '20081130184517-0000145A4C812E4D-66CE94C4', 3, '30ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8DC85D71-B7F38939', '20081130184517-0000145A4C812E4D-66CE94C4', 4, '50ml sok z marakuii')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8E173B07-565968A6', '20081130184517-0000145A4C812E4D-66CE94C4', 5, '25ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8E2F8DD0-D44CCD40', '20081130184517-0000145A4C812E4D-66CE94C4', 6, '25ml sok z czarnej porzeczki (ew. syrop)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8E467BC1-26257C49', '20081130184517-0000145A4C812E4D-66CE94C4', 7, '25ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8E5D643D-BB108638', '20081130184517-0000145A4C812E4D-66CE94C4', 8, '25ml sok z ananasa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8E742D14-CB1EFC0D', '20081130184517-0000145A4C812E4D-66CE94C4', 9, 'imbir')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8E8AE246-B644D1E6', '20081130184517-0000145A4C812E4D-66CE94C4', 10, 'ananas')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8EA1B606-2DC6D2DD', '20081130184517-0000145A4C812E4D-66CE94C4', 11, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184518-0000145A8EB88569-7CE8E0EE', '20081130184517-0000145A4C812E4D-66CE94C4', 12, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184525-0000145C3D05FF68-065910C1', '20081130184524-0000145BF80A04AB-C33C2F69', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184525-0000145C3D2371E7-FE13C177', '20081130184524-0000145BF80A04AB-C33C2F69', 2, '25ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184525-0000145C3D3D2BB7-56C2AB65', '20081130184524-0000145BF80A04AB-C33C2F69', 3, '25ml jasny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184525-0000145C3D575FC0-F2470CF3', '20081130184524-0000145BF80A04AB-C33C2F69', 4, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184525-0000145C3D70E52F-3C7FE827', '20081130184524-0000145BF80A04AB-C33C2F69', 5, '25ml sok z marakuii')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184525-0000145C3D8898A5-FD0B550E', '20081130184524-0000145BF80A04AB-C33C2F69', 6, 'lima Skórka limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184533-0000145DF67CFA3B-1F0133AB', '20081130184532-0000145DB9EA150C-1BF6F294', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184533-0000145DF69A662D-D6B0ECC2', '20081130184532-0000145DB9EA150C-1BF6F294', 2, '50ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184533-0000145DF6B2E386-E250CB69', '20081130184532-0000145DB9EA150C-1BF6F294', 3, '15ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184533-0000145DF6CA629B-3EE31915', '20081130184532-0000145DB9EA150C-1BF6F294', 4, '5ml miód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184533-0000145DF6E26900-F6AC8995', '20081130184532-0000145DB9EA150C-1BF6F294', 5, '50ml lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184533-0000145DF6F9CCCD-681D2EE0', '20081130184532-0000145DB9EA150C-1BF6F294', 6, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184540-0000145FA8029A01-1F8FA3B0', '20081130184539-0000145F54764287-E9320E2F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184540-0000145FA81D266F-4902EA86', '20081130184539-0000145F54764287-E9320E2F', 2, '50ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184540-0000145FA834C757-21674455', '20081130184539-0000145F54764287-E9320E2F', 3, '15ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184540-0000145FA84BE0EF-78A127FE', '20081130184539-0000145F54764287-E9320E2F', 4, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184540-0000145FA8634B48-543A95CF', '20081130184539-0000145F54764287-E9320E2F', 5, 'sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184540-0000145FA87AB143-A80A8F57', '20081130184539-0000145F54764287-E9320E2F', 6, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184548-00001461833AA97A-2AA8E959', '20081130184547-000014613A580C02-5F48E811', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184548-00001461838BDE2C-BD58931F', '20081130184547-000014613A580C02-5F48E811', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184548-0000146183A66525-E2EA09D2', '20081130184547-000014613A580C02-5F48E811', 3, '15ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184548-0000146183BE79BA-FC5FE56B', '20081130184547-000014613A580C02-5F48E811', 4, '15ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184548-0000146183D5B86C-A10AB8EC', '20081130184547-000014613A580C02-5F48E811', 5, 'cukier puder')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184548-0000146183ED114F-DFB6211A', '20081130184547-000014613A580C02-5F48E811', 6, 'miêta')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184557-000014638580BCD8-AF7ABE86', '20081130184555-000014634122582C-9E674459', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184557-00001463859E27B3-8ADBA00B', '20081130184555-000014634122582C-9E674459', 2, '25ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184557-0000146385B63B31-8A0E0B73', '20081130184555-000014634122582C-9E674459', 3, '25ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184557-0000146385CDFAA8-6694F58A', '20081130184555-000014634122582C-9E674459', 4, '25ml jasny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184557-0000146385E51CFB-EE9282DF', '20081130184555-000014634122582C-9E674459', 5, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184557-0000146385FC6E3A-2FA999FF', '20081130184555-000014634122582C-9E674459', 6, '75ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184557-000014638613FDAD-56AEF39B', '20081130184555-000014634122582C-9E674459', 7, '75ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184557-00001463862B28BA-0AFC8220', '20081130184555-000014634122582C-9E674459', 8, 'plasterki ananasa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184604-00001465306F456C-6A0B867F', '20081130184603-00001464FD159823-4193C4A7', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184604-00001465308E2237-C276C908', '20081130184603-00001464FD159823-4193C4A7', 2, '30ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184604-0000146530A7828C-9C596177', '20081130184603-00001464FD159823-4193C4A7', 3, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184604-0000146530BEE99E-29D06998', '20081130184603-00001464FD159823-4193C4A7', 4, '30ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184604-0000146530D646DE-D1DB073D', '20081130184603-00001464FD159823-4193C4A7', 5, '25ml likier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184604-0000146530ECB868-6CA012D3', '20081130184603-00001464FD159823-4193C4A7', 6, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184611-00001466DFF07D79-28DF42C2', '20081130184610-00001466AD9FF417-331B89D9', 1, 'Lód t³uczony')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184611-00001466E00A9869-8DAEE4FF', '20081130184610-00001466AD9FF417-331B89D9', 2, '60ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184611-00001466E0221437-3516A0D6', '20081130184610-00001466AD9FF417-331B89D9', 3, '25ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184611-00001466E03F1420-8B88F2E2', '20081130184610-00001466AD9FF417-331B89D9', 4, '5ml wódka gorzka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184611-00001466E05B0F39-997B101E', '20081130184610-00001466AD9FF417-331B89D9', 5, '15ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184611-00001466E0736548-648FC10F', '20081130184610-00001466AD9FF417-331B89D9', 6, 'trzy ga³¹zki miêty')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184611-00001466E08A73F6-8E2D999B', '20081130184610-00001466AD9FF417-331B89D9', 7, '50ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184611-00001466E0A10EF7-176391F9', '20081130184610-00001466AD9FF417-331B89D9', 8, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184619-00001468B534D7F3-4C5B79AB', '20081130184618-00001468697AD65B-D65F5FDE', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184619-00001468B5522CFA-7D153D04', '20081130184618-00001468697AD65B-D65F5FDE', 2, '40ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184619-00001468B5A220D6-D3240338', '20081130184618-00001468697AD65B-D65F5FDE', 3, '75ml lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184619-00001468B5BEA112-BF73C6E8', '20081130184618-00001468697AD65B-D65F5FDE', 4, '75ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184619-00001468B5DAC373-CD16F9B6', '20081130184618-00001468697AD65B-D65F5FDE', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184619-00001468B5F2B74C-B3B4D65C', '20081130184618-00001468697AD65B-D65F5FDE', 6, 'plasterek grejpfruta')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184627-0000146A867F366D-ADA22B0D', '20081130184626-0000146A4E2EC077-149DC47B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184627-0000146A8699083F-F5742358', '20081130184626-0000146A4E2EC077-149DC47B', 2, '30ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184627-0000146A86B06697-6086BB1C', '20081130184626-0000146A4E2EC077-149DC47B', 3, '10ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184627-0000146A86C6E538-E9580CC4', '20081130184626-0000146A4E2EC077-149DC47B', 4, '150ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184627-0000146A86E40924-8CE9F981', '20081130184626-0000146A4E2EC077-149DC47B', 5, 'plasterek grejpfruta')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184635-0000146C7592A388-DCE7F85F', '20081130184634-0000146C1E18DEEF-24A00C5A', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184635-0000146C75B1384E-EED54DFA', '20081130184634-0000146C1E18DEEF-24A00C5A', 2, '40ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184635-0000146C75C99B75-B0FCA155', '20081130184634-0000146C1E18DEEF-24A00C5A', 3, '40ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184635-0000146C75E32F14-AA0C3033', '20081130184634-0000146C1E18DEEF-24A00C5A', 4, '5ml creme de cacao')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184635-0000146C75FD5834-65A45C87', '20081130184634-0000146C1E18DEEF-24A00C5A', 5, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184643-0000146E438EA25A-395FF404', '20081130184642-0000146E08D1B7DD-F2DCC135', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184643-0000146E43A89947-634260F8', '20081130184642-0000146E08D1B7DD-F2DCC135', 2, '50ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184643-0000146E43C49C04-FB99C570', '20081130184642-0000146E08D1B7DD-F2DCC135', 3, '10g cukier puder')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184643-0000146E43E0765F-6A17C66A', '20081130184642-0000146E08D1B7DD-F2DCC135', 4, '25ml sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184643-0000146E43F8C6FA-5CAC886B', '20081130184642-0000146E08D1B7DD-F2DCC135', 5, '75ml sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184643-0000146E44102669-2949C264', '20081130184642-0000146E08D1B7DD-F2DCC135', 6, '50ml woda gazowana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184643-0000146E44298D49-FD3C28EF', '20081130184642-0000146E08D1B7DD-F2DCC135', 7, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184643-0000146E44414DD8-87F7C4E6', '20081130184642-0000146E08D1B7DD-F2DCC135', 8, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184643-0000146E44588FD0-F19DE9CD', '20081130184642-0000146E08D1B7DD-F2DCC135', 9, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184650-00001470084591B9-E6AD2E20', '20081130184649-0000146FCC2474B7-6BC6BF2E', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184650-000014700895BF6C-C7F86082', '20081130184649-0000146FCC2474B7-6BC6BF2E', 2, '30ml jasny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184650-0000147008ADD2EA-12D08F8A', '20081130184649-0000146FCC2474B7-6BC6BF2E', 3, '25ml sok z marakui')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184650-0000147008C52658-B0D7DC71', '20081130184649-0000146FCC2474B7-6BC6BF2E', 4, '10ml syrop ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184650-0000147008DBB0FB-036B78FB', '20081130184649-0000146FCC2474B7-6BC6BF2E', 5, '25ml sok z czerwonych winogron')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184650-0000147008F470E4-F5D5296B', '20081130184649-0000146FCC2474B7-6BC6BF2E', 6, 'plasterek pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184658-00001471D6658F3B-527D640D', '20081130184657-000014719C1EC08F-7AB74F6A', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184658-00001471D684E0CA-A2E151B2', '20081130184657-000014719C1EC08F-7AB74F6A', 2, '25ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184658-00001471D69F74DC-796A112B', '20081130184657-000014719C1EC08F-7AB74F6A', 3, '25ml sok grapefruitowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184658-00001471D6B6A675-D16864F0', '20081130184657-000014719C1EC08F-7AB74F6A', 4, '25ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184658-00001471D6CDAD7F-9CC2E642', '20081130184657-000014719C1EC08F-7AB74F6A', 5, '25ml sok z czerwonych winogron')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184658-00001471D6E554F4-B7E07E47', '20081130184657-000014719C1EC08F-7AB74F6A', 6, 'plasterek grapefruita')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184705-000014737691A69D-B19D93C6', '20081130184704-000014733541E71C-A0A2A712', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184705-0000147376AC1594-1D8DA0CC', '20081130184704-000014733541E71C-A0A2A712', 2, '30ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184705-0000147376C314FB-DD2752A9', '20081130184704-000014733541E71C-A0A2A712', 3, '50ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184705-0000147376DA0BA7-288E706C', '20081130184704-000014733541E71C-A0A2A712', 4, '25ml creme de banane')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184705-0000147376F0698B-1DEFB247', '20081130184704-000014733541E71C-A0A2A712', 5, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184713-0000147555DFA9E9-EF166EDB', '20081130184712-000014751E073BEE-47191BBC', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184713-0000147555F8851B-F7AD21F1', '20081130184712-000014751E073BEE-47191BBC', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184713-00001475560F6822-D1D4BB3E', '20081130184712-000014751E073BEE-47191BBC', 3, '30ml krem kokosowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184713-0000147556263ACB-AE7E82DA', '20081130184712-000014751E073BEE-47191BBC', 4, '50ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184713-00001475563CC79D-4152F8D7', '20081130184712-000014751E073BEE-47191BBC', 5, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184713-0000147556546428-B660881C', '20081130184712-000014751E073BEE-47191BBC', 6, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184713-00001475566D3E43-FEA37CC9', '20081130184712-000014751E073BEE-47191BBC', 7, 'plasterek ananasa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184728-00001478E4A20F72-F6FB45D5', '20081130184727-00001478A87B50DF-C4F68BE8', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184728-00001478E4BB487E-67E91CC7', '20081130184727-00001478A87B50DF-C4F68BE8', 2, '20ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184728-00001478E4D598E7-2CBA60D1', '20081130184727-00001478A87B50DF-C4F68BE8', 3, '25ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184728-00001478E4EFCE07-8F321E54', '20081130184727-00001478A87B50DF-C4F68BE8', 4, '50ml brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184728-00001478E50958EB-9EB876C6', '20081130184727-00001478A87B50DF-C4F68BE8', 5, '50ml likier Galliano')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184728-00001478E524A33B-F9A43A9F', '20081130184727-00001478A87B50DF-C4F68BE8', 6, '5ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184728-00001478E53F4C08-FC3429EC', '20081130184727-00001478A87B50DF-C4F68BE8', 7, '50ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184728-00001478E55D5CC2-085671E1', '20081130184727-00001478A87B50DF-C4F68BE8', 8, '100ml lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184728-00001478E574E7D8-5D008F44', '20081130184727-00001478A87B50DF-C4F68BE8', 9, 'plasterek cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184728-00001478E58BE3F8-1E254695', '20081130184727-00001478A87B50DF-C4F68BE8', 10, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184735-0000147A7C32DA1E-6107D6F0', '20081130184734-0000147A4AA6B8FF-7EF27835', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184735-0000147A7C50FF94-9D95EB67', '20081130184734-0000147A4AA6B8FF-7EF27835', 2, '30ml z³oty rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184735-0000147A7C691887-667866A2', '20081130184734-0000147A4AA6B8FF-7EF27835', 3, '50ml krem kokosowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184735-0000147A7C824362-783060D0', '20081130184734-0000147A4AA6B8FF-7EF27835', 4, '5ml ciemny rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184735-0000147A7C9A63F9-5DA0D8C5', '20081130184734-0000147A4AA6B8FF-7EF27835', 5, '25ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184735-0000147A7CB2E498-96B8FA74', '20081130184734-0000147A4AA6B8FF-7EF27835', 6, '25ml sok grejpfrutowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184735-0000147A7CCBF0E5-723A3765', '20081130184734-0000147A4AA6B8FF-7EF27835', 7, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184743-0000147C443BCAE7-09A67BF9', '20081130184742-0000147C00D942D4-047D820E', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184743-0000147C4458D6D1-E9D57B7F', '20081130184742-0000147C00D942D4-047D820E', 2, '50ml z³oty rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184743-0000147C446F9804-3FBE6FF2', '20081130184742-0000147C00D942D4-047D820E', 3, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184743-0000147C448BC438-D41CD7F6', '20081130184742-0000147C00D942D4-047D820E', 4, '10ml syrop migda³owy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184743-0000147C44A3A69B-A884D089', '20081130184742-0000147C00D942D4-047D820E', 5, '10ml œliwowica')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184743-0000147C44BAD091-E286D0E5', '20081130184742-0000147C00D942D4-047D820E', 6, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184751-0000147E0CE96DFF-658377EC', '20081130184749-0000147DCC93A579-BFB4285F', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184751-0000147E0D042A71-B778B644', '20081130184749-0000147DCC93A579-BFB4285F', 2, '50ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184751-0000147E0D1B028F-669B5167', '20081130184749-0000147DCC93A579-BFB4285F', 3, '5ml syrop trzcinowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184751-0000147E0D318C1A-4F666F31', '20081130184749-0000147DCC93A579-BFB4285F', 4, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184758-0000147FDA3E1D90-09695F13', '20081130184757-0000147FA3203648-C88A187B', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184758-0000147FDA59F5BD-6AE50F8C', '20081130184757-0000147FA3203648-C88A187B', 2, '30ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184758-0000147FDA70BC65-D948563B', '20081130184757-0000147FA3203648-C88A187B', 3, '10ml krem kokosowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184758-0000147FDABD29C6-AFD91294', '20081130184757-0000147FA3203648-C88A187B', 4, '10ml amaretto')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184758-0000147FDAD521FC-01302F7B', '20081130184757-0000147FA3203648-C88A187B', 5, '25ml sok z marakuii')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184758-0000147FDAEB8327-8E3447DD', '20081130184757-0000147FA3203648-C88A187B', 6, '50ml sok ananasowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184758-0000147FDB01CE7E-B5737610', '20081130184757-0000147FA3203648-C88A187B', 7, '25ml smietana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184758-0000147FDB1862F3-D96D20BB', '20081130184757-0000147FA3203648-C88A187B', 8, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184806-0000148191B7999E-B1769907', '20081130184804-000014811C52894F-E1469DDD', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184806-0000148191D1F608-B70745F4', '20081130184804-000014811C52894F-E1469DDD', 2, '30ml bia³y rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184806-0000148191E91514-F19733FF', '20081130184804-000014811C52894F-E1469DDD', 3, '25ml sok z mango')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184806-0000148192001C1F-2A26CCF5', '20081130184804-000014811C52894F-E1469DDD', 4, '50ml Malibu')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184806-00001481921676BD-8E707DD5', '20081130184804-000014811C52894F-E1469DDD', 5, '25ml sok z brzoskwini')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184806-00001481922DC4B6-6E0DDF4A', '20081130184804-000014811C52894F-E1469DDD', 6, '50ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184806-0000148192446644-183B84AB', '20081130184804-000014811C52894F-E1469DDD', 7, 'plasterek limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184903-0000148EDE5B3C33-A7C6CD59', '20081130184902-0000148E9943A9AB-C2C6D389', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184903-0000148EDE757F84-7B6B1321', '20081130184902-0000148E9943A9AB-C2C6D389', 2, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184903-0000148EDE8D8FBC-E9515479', '20081130184902-0000148E9943A9AB-C2C6D389', 3, '25ml creme de banana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184903-0000148EDEA4794F-7C273FD1', '20081130184902-0000148E9943A9AB-C2C6D389', 4, '25 ml œmietana')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184903-0000148EDEBAD2D7-43EF9BEB', '20081130184902-0000148E9943A9AB-C2C6D389', 5, 'gorzka czekolada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184911-00001490C22E1113-85B503C3', '20081130184910-000014908EC371FD-823AD9BE', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184911-00001490C24871DA-0B03123B', '20081130184910-000014908EC371FD-823AD9BE', 2, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184911-00001490C25F353C-D77F42A3', '20081130184910-000014908EC371FD-823AD9BE', 3, '15ml brzoskwiniona brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184911-00001490C275EFE2-DEA6EBB8', '20081130184910-000014908EC371FD-823AD9BE', 4, '20ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184911-00001490C28C5225-94B380E1', '20081130184910-000014908EC371FD-823AD9BE', 5, '25ml sok brzoskwiniowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184911-00001490C2A38A4A-20ED66E0', '20081130184910-000014908EC371FD-823AD9BE', 6, 'brzoswinia')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184918-00001492549706EA-2BA1155B', '20081130184917-000014921F83F2CE-D1977145', 1, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184918-0000149254AB5D5D-AC0385ED', '20081130184917-000014921F83F2CE-D1977145', 2, '30ml Benedictine')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184927-000014946ABA1C0E-FDC64F33', '20081130184926-000014942D492528-2609E5BB', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184927-000014946AD75E88-8CCA1B85', '20081130184926-000014942D492528-2609E5BB', 2, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184927-000014946AEE8538-4B178627', '20081130184926-000014942D492528-2609E5BB', 3, '10ml Kahlua')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184927-000014946B058041-ACA10647', '20081130184926-000014942D492528-2609E5BB', 4, '15ml czarna kawa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184933-00001495F3DFB5DC-17C8ABCA', '20081130184932-00001495BC6AABEE-40FAD40D', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184933-00001495F3F9C929-92E7ACE3', '20081130184932-00001495BC6AABEE-40FAD40D', 2, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184933-00001495F410D490-94B1E6A4', '20081130184932-00001495BC6AABEE-40FAD40D', 3, '5ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184933-00001495F4274CA6-84F8190B', '20081130184932-00001495BC6AABEE-40FAD40D', 4, '15ml czerwony wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184933-00001495F43E5A3D-AA80BE21', '20081130184932-00001495BC6AABEE-40FAD40D', 5, '15ml wytrawny wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184940-0000149773C137F0-0F2D1A44', '20081130184939-000014973E96FF53-A3BCD0A8', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184940-0000149773DDF9A5-A1598FD0', '20081130184939-000014973E96FF53-A3BCD0A8', 2, '20ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184940-0000149773F525C9-17D42A03', '20081130184939-000014973E96FF53-A3BCD0A8', 3, '25ml pomarañczowy curacao')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184940-00001497740CC36C-496EBAB6', '20081130184939-000014973E96FF53-A3BCD0A8', 4, 'ga³ka muszkatowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184947-000014993C717B41-291684FA', '20081130184946-0000149900C332E0-A3906B18', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184947-000014993C8B63FE-AF09DE4D', '20081130184946-0000149900C332E0-A3906B18', 2, '25ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184947-000014993CA570BE-4D7ED6F5', '20081130184946-0000149900C332E0-A3906B18', 3, '25ml creme de cacao (br¹zowy)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184947-000014993CBD048E-55F1F945', '20081130184946-0000149900C332E0-A3906B18', 4, 'ga³ka muszkatowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184954-0000149AD57A00A4-ED5528AA', '20081130184953-0000149AA3E3063D-1BBF6A25', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184954-0000149AD597D213-C787A450', '20081130184953-0000149AA3E3063D-1BBF6A25', 2, '15ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184954-0000149AD5AFA52F-AF85DF78', '20081130184953-0000149AA3E3063D-1BBF6A25', 3, '4 listki miêty')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184954-0000149AD5D7E3DD-5DE51CAD', '20081130184953-0000149AA3E3063D-1BBF6A25', 4, '25ml woda sodowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130184954-0000149AD5EFE187-92E705C8', '20081130184953-0000149AA3E3063D-1BBF6A25', 5, 'cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185001-0000149C81B3DC2D-4C4FD93C', '20081130185000-0000149C41C56A38-01965F10', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185001-0000149C81CC8EFF-F3A76FC1', '20081130185000-0000149C41C56A38-01965F10', 2, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185001-0000149C81E709F7-0BEA2342', '20081130185000-0000149C41C56A38-01965F10', 3, '15ml cherry brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185001-0000149C8200A30A-F8D20E0F', '20081130185000-0000149C41C56A38-01965F10', 4, '5ml cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185001-0000149C821841C4-C8C7A028', '20081130185000-0000149C41C56A38-01965F10', 5, 'sok')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185001-0000149C822F2C6F-BF025A4D', '20081130185000-0000149C41C56A38-01965F10', 6, 'skórka z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185010-0000149E8154196E-1F27B6D9', '20081130185009-0000149E42DE9706-489C1E89', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185010-0000149E816E5404-75C47B49', '20081130185009-0000149E42DE9706-489C1E89', 2, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185010-0000149E81855B0E-44B5FB19', '20081130185009-0000149E42DE9706-489C1E89', 3, '25ml rum (ciemny)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185010-0000149E819B9A65-C1F85024', '20081130185009-0000149E42DE9706-489C1E89', 4, '25ml creme de cacao')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185010-0000149E81B23566-9EF4EED0', '20081130185009-0000149E42DE9706-489C1E89', 5, 'plaster pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185017-000014A0357EEFFD-1C03E053', '20081130185016-0000149FFBE830F3-F086C4AF', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185017-000014A035998526-3DC67D63', '20081130185016-0000149FFBE830F3-F086C4AF', 2, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185017-000014A035B0A0EC-D8C3B402', '20081130185016-0000149FFBE830F3-F086C4AF', 3, '10ml creme de cassis')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185017-000014A035C7A053-F85B102C', '20081130185016-0000149FFBE830F3-F086C4AF', 4, '5ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185017-000014A035DE34C8-D9B77384', '20081130185016-0000149FFBE830F3-F086C4AF', 5, 'plaster cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185024-000014A1A977B051-CE980E6F', '20081130185023-000014A1732C1906-81669191', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185024-000014A1A99BABBA-FF8682AE', '20081130185023-000014A1732C1906-81669191', 2, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185024-000014A1A9B48D79-D061AEF2', '20081130185023-000014A1732C1906-81669191', 3, '10ml czerwony wermut')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185024-000014A1A9CB6E51-ED35C2C2', '20081130185023-000014A1732C1906-81669191', 4, '5ml grenadyna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185024-000014A1A9E25F88-B74CBDFF', '20081130185023-000014A1732C1906-81669191', 5, '5ml sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185032-000014A3A9C00F71-3D1503C0', '20081130185031-000014A373588D6C-13E78ADF', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185032-000014A3A9DE61A5-2647C5D8', '20081130185031-000014A373588D6C-13E78ADF', 2, '25ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185032-000014A3A9F6F5E9-5638C3D0', '20081130185031-000014A373588D6C-13E78ADF', 3, '10ml cherry brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185032-000014A3AA0D75A2-BC40A307', '20081130185031-000014A373588D6C-13E78ADF', 4, '10ml Chartreuse')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185032-000014A3AA245334-429B5DEF', '20081130185031-000014A373588D6C-13E78ADF', 5, 'wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185039-000014A55202C166-3418702D', '20081130185038-000014A515C59E61-E436E9BB', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185039-000014A5521D35D2-58F39E83', '20081130185038-000014A515C59E61-E436E9BB', 2, '25ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185039-000014A55233F190-9FC7742E', '20081130185038-000014A515C59E61-E436E9BB', 3, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185039-000014A5524A954C-7CFC22FF', '20081130185038-000014A515C59E61-E436E9BB', 4, 'bia³ka jajek')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185039-000014A552610904-D89DA1DA', '20081130185038-000014A515C59E61-E436E9BB', 5, '5ml cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185046-000014A6FCE8CE59-A6782CE0', '20081130185045-000014A6C48F0A25-9FDF9D16', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185046-000014A6FD054A37-F8B75E14', '20081130185045-000014A6C48F0A25-9FDF9D16', 2, '25ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185046-000014A6FD2045F4-30A189AA', '20081130185045-000014A6C48F0A25-9FDF9D16', 3, '10ml Cointreau')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185046-000014A6FD37CEDB-B88B7144', '20081130185045-000014A6C48F0A25-9FDF9D16', 4, '75ml lody cytrynowe (najlepiej sorbet)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185046-000014A6FD4E2633-B4849C43', '20081130185045-000014A6C48F0A25-9FDF9D16', 5, 'plaster cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185053-000014A891FA49CD-9A56EC56', '20081130185052-000014A8569153A8-12310065', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185053-000014A8924D6F06-31599537', '20081130185052-000014A8569153A8-12310065', 2, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185053-000014A892654F3A-C1B2BD90', '20081130185052-000014A8569153A8-12310065', 3, '25ml d¿in')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185053-000014A8927C4188-77357A14', '20081130185052-000014A8569153A8-12310065', 4, '15ml Calvados')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185100-000014AA31A0F7B6-BAB070F9', '20081130185059-000014A9F207B49A-5045A614', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185100-000014AA31BB44D8-6A20DC54', '20081130185059-000014A9F207B49A-5045A614', 2, '25ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185100-000014AA31D2C733-9B1E9A69', '20081130185059-000014A9F207B49A-5045A614', 3, '25ml rum')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185100-000014AA31E9657A-C35A87F9', '20081130185059-000014A9F207B49A-5045A614', 4, '10ml sok z limy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185100-000014AA31FFC019-4E5669BB', '20081130185059-000014A9F207B49A-5045A614', 5, 'truskawki')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185108-000014AC02EAD10D-A9F528EF', '20081130185107-000014ABCD5F6CF0-AF94CE8D', 1, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185108-000014AC0304E459-B2F228B2', '20081130185107-000014ABCD5F6CF0-AF94CE8D', 2, '30ml koniak')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185108-000014AC031BA12F-16BDCD2B', '20081130185107-000014ABCD5F6CF0-AF94CE8D', 3, '10ml cherry brandy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185108-000014AC0332D954-05C00D57', '20081130185107-000014ABCD5F6CF0-AF94CE8D', 4, '5ml syrop cukrowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185108-000014AC03493CAE-A53BB59E', '20081130185107-000014ABCD5F6CF0-AF94CE8D', 5, 'angostura')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185524-000014E78E799064-0C5ECC31', '20081130185508-000014E3D97A3506-4B9BAA16', 1, 'Blue Curacao (1/3 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185524-000014E78EDF3E74-E9631ABF', '20081130185508-000014E3D97A3506-4B9BAA16', 2, 'Ouzo (1/3 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130185524-000014E78EFEC694-D4A961EE', '20081130185508-000014E3D97A3506-4B9BAA16', 3, 'Likier bananowy (1/3 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190204-00001544B577CA3B-868C53DA', '20081130190156-00001542FD16A51C-323B76C6', 1, 'Gin (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190204-00001544B5AC7A54-760E8D27', '20081130190156-00001542FD16A51C-323B76C6', 2, 'Apricot Brandy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190204-00001544B66ACA14-CDD7F127', '20081130190156-00001542FD16A51C-323B76C6', 3, 'Likier any¿owy (2 barowe ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190204-00001544B6A04984-FEC6D6D4', '20081130190156-00001542FD16A51C-323B76C6', 4, 'Sok cytrynowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190204-00001544B740313C-D1487C5D', '20081130190156-00001542FD16A51C-323B76C6', 5, 'Woda sodowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190204-00001544B760750F-41AB9B3F', '20081130190156-00001542FD16A51C-323B76C6', 6, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190216-000015477E4FB5F3-DC016E22', '20081130190213-00001546DB5A30AA-78F22397', 1, 'Campari (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190216-000015477E9BF350-DFD60094', '20081130190213-00001546DB5A30AA-78F22397', 2, 'Wódka (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190216-000015477ED88CCF-D81B0730', '20081130190213-00001546DB5A30AA-78F22397', 3, 'Likier pomarañczowy (1 ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190216-000015477EF5AB46-FA719FBE', '20081130190213-00001546DB5A30AA-78F22397', 4, 'Sok cytrynowy (1 ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190216-000015477F10F6AD-32F7953F', '20081130190213-00001546DB5A30AA-78F22397', 5, 'Bitter pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190216-000015477FA87AC6-B9E8D7EA', '20081130190213-00001546DB5A30AA-78F22397', 6, 'Owoce kumkwatu')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190216-000015477FCA40E8-8A872F0B', '20081130190213-00001546DB5A30AA-78F22397', 7, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190226-00001549D4E6D8B8-AFC8B05B', '20081130190224-0000154965296CC9-40780E91', 1, 'Likier Apricot Brandy (1 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190226-00001549D5097D79-7881410E', '20081130190224-0000154965296CC9-40780E91', 2, 'Likier Triple Sec (1 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190226-00001549D552B637-9E0C8C55', '20081130190224-0000154965296CC9-40780E91', 3, 'Limetka (sztuk 1 - sok)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190226-00001549D5B04BF5-589BAA57', '20081130190224-0000154965296CC9-40780E91', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190234-0000154BD6DD5614-0A1B8F84', '20081130190233-0000154B8CC1A103-CBC4F7C5', 1, 'Likier kokosowy Ajacoco (60 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190234-0000154BD6FF4D51-7AFD2B5B', '20081130190233-0000154B8CC1A103-CBC4F7C5', 2, 'Mleko skondensowane nies³odzone lub œmietanka (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190234-0000154BD71C7026-4C625625', '20081130190233-0000154B8CC1A103-CBC4F7C5', 3, 'Tarta czekolada (szczypta)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190234-0000154BD7A33F8F-CC04DB8A', '20081130190233-0000154B8CC1A103-CBC4F7C5', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190244-0000154E2BA2844F-E1CC5AE0', '20081130190243-0000154DDEB40A0E-23535503', 1, 'Brandy (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190244-0000154E2BC8FF34-DBE53455', '20081130190243-0000154DDEB40A0E-23535503', 2, 'Likier kakaowy (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190244-0000154E2BE555F7-67DD7E80', '20081130190243-0000154DDEB40A0E-23535503', 3, 'Œmietanka (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190244-0000154E2BFF87D2-4C7703A5', '20081130190243-0000154DDEB40A0E-23535503', 4, 'Ga³ka muszkato³owa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190244-0000154E2C19B54F-84A532B1', '20081130190243-0000154DDEB40A0E-23535503', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190253-00001550381587D9-7A432130', '20081130190252-0000154FE428E7DB-21C74C55', 1, 'Likier Amaretto (1 1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190253-000015503835A11D-5F035C08', '20081130190252-0000154FE428E7DB-21C74C55', 2, 'Œmietanka (1 1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190253-000015503852ABEF-A0310E88', '20081130190252-0000154FE428E7DB-21C74C55', 3, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190302-000015524646C013-C1E52EAF', '20081130190300-00001551DC45D760-3ABBBA7F', 1, 'Likier Amaretto (1 1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190302-000015524665D486-96E62E20', '20081130190300-00001551DC45D760-3ABBBA7F', 2, 'Sok cytrynowy (3/4 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190302-00001552468053DB-287A2704', '20081130190300-00001551DC45D760-3ABBBA7F', 3, 'Pomarañcz (plasterek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190302-000015524699FA07-52D8D749', '20081130190300-00001551DC45D760-3ABBBA7F', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190311-00001554546AE219-F9AC8503', '20081130190310-0000155424982D42-89E858F5', 1, 'Likier Amaretto (1 1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190311-0000155454897052-6A91596A', '20081130190310-0000155424982D42-89E858F5', 2, 'Bia³y Creme de Menthe (3/4 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190311-0000155454A45753-3075E77B', '20081130190310-0000155424982D42-89E858F5', 3, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190323-00001557162FE9F6-2285E6EF', '20081130190322-00001556D33E565B-59A477FA', 1, 'Amaretto di Saronno (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190323-0000155716607BE4-1000E62B', '20081130190322-00001556D33E565B-59A477FA', 2, 'Bourbon Whisky (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190323-00001557167DB8E9-360B85F1', '20081130190322-00001556D33E565B-59A477FA', 3, 'Bia³y Creme de Menthe (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190323-0000155716988900-49A4B09D', '20081130190322-00001556D33E565B-59A477FA', 4, 'Pomarañcz (1/4 plastra)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190323-0000155716B29B35-12AD591A', '20081130190322-00001556D33E565B-59A477FA', 5, 'Miêta (1 ga³¹zka )')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190323-0000155716D0E6DD-7B8508A5', '20081130190322-00001556D33E565B-59A477FA', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190331-00001558EDE54CFD-D164D18A', '20081130190330-00001558ADE5CF0B-8A48BC56', 1, 'Brandy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190331-00001558EE0369B8-9A8F99FC', '20081130190330-00001558ADE5CF0B-8A48BC56', 2, 'Wytrawny Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190331-00001558EEB2748A-7EA1FF50', '20081130190330-00001558ADE5CF0B-8A48BC56', 3, 'Creme de Menthe (1/2 ³y¿eczki barowej)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190331-00001558EED274B4-EE149783', '20081130190330-00001558ADE5CF0B-8A48BC56', 4, 'Sok pomarañczowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190331-00001558EEF07EE2-2D5DFEE8', '20081130190330-00001558ADE5CF0B-8A48BC56', 5, 'Syrop grenadynowy (1/2 ³y¿eczki barowej)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190331-00001558EF32A536-F5159816', '20081130190330-00001558ADE5CF0B-8A48BC56', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190341-0000155B5616B0E6-C1B16978', '20081130190339-0000155AEE21F31F-65BAB9FF', 1, 'Likier Cointreau (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190341-0000155B56350660-C35FF051', '20081130190339-0000155AEE21F31F-65BAB9FF', 2, 'Sok cytrynowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190341-0000155B564F57E0-2F06084D', '20081130190339-0000155AEE21F31F-65BAB9FF', 3, 'Sok pomarañczowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190341-0000155B5669F4AD-37A2982D', '20081130190339-0000155AEE21F31F-65BAB9FF', 4, 'Woda sodowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190341-0000155B5683E73C-9D6CDC68', '20081130190339-0000155AEE21F31F-65BAB9FF', 5, 'Miêta (listek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190341-0000155B56C0585A-C3335B1E', '20081130190339-0000155AEE21F31F-65BAB9FF', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190349-0000155D42AA9DAF-3D24E93C', '20081130190348-0000155D0968CF4A-EFE5EDBD', 1, 'Calvados Morin (7 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190349-0000155D42CA1AE5-CC0FD9F6', '20081130190348-0000155D0968CF4A-EFE5EDBD', 2, 'Creme de Cassis Boudier (4 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190349-0000155D42E80227-695A685B', '20081130190348-0000155D0968CF4A-EFE5EDBD', 3, 'Œwierzy sok pomarañczowy (14 ml )')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190349-0000155D43025A34-808DFADF', '20081130190348-0000155D0968CF4A-EFE5EDBD', 4, 'Sok cytrynowy (2 krople)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190349-0000155D431C0B49-39CD582B', '20081130190348-0000155D0968CF4A-EFE5EDBD', 5, 't³uczony lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190401-0000155FE604A24B-F3B6C086', '20081130190359-0000155F89E27A91-62D26C23', 1, 'Gordon''s Dry Gin (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190401-0000155FE623DCF0-F6310D54', '20081130190359-0000155F89E27A91-62D26C23', 2, 'Likier morelowy Barack Liqueur (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190401-0000155FE63DD096-FB2358D3', '20081130190359-0000155F89E27A91-62D26C23', 3, 'Wiœnia koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190401-0000155FE65769AA-549E2BDE', '20081130190359-0000155F89E27A91-62D26C23', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190409-00001561F9C420BF-895B018E', '20081130190408-00001561A66C5672-D1D2B13B', 1, 'Bailey''s Irish Cream (25 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190409-00001561F9E6D80C-C5C463DD', '20081130190408-00001561A66C5672-D1D2B13B', 2, 'Likier Kahula (25 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190409-00001561FA0590D5-44A634B2', '20081130190408-00001561A66C5672-D1D2B13B', 3, 'Likier Grand Marnier (25 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190409-00001561FA23FE51-A3650B08', '20081130190408-00001561A66C5672-D1D2B13B', 4, 'Spirytus (1 ml).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190417-00001563C3DCA9D6-C2D5C92B', '20081130190416-0000156370683061-17F9F0B1', 1, 'Likier Khalua (50ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190417-00001563C3FCA7D1-41C4876E', '20081130190416-0000156370683061-17F9F0B1', 2, 'Likier Baileys (50ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190417-00001563C4179BEB-C144BA30', '20081130190416-0000156370683061-17F9F0B1', 3, 'Likier Cointreau (25ml).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190429-0000156699431C9B-33A999A7', '20081130190428-000015664BAA3678-2058B84F', 1, 'Likier Cointreau (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190429-0000156699671061-3DF502CC', '20081130190428-000015664BAA3678-2058B84F', 2, 'Wódka (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190429-0000156699833DAC-D8FB3932', '20081130190428-000015664BAA3678-2058B84F', 3, 'Sok pomarañczowy (60 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190429-0000156699A1D6CF-51A216F0', '20081130190428-000015664BAA3678-2058B84F', 4, 'Cytryna (plasterek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190429-0000156699BBA9B9-A9F94941', '20081130190428-000015664BAA3678-2058B84F', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190437-000015686A7A1A2B-8246C688', '20081130190436-00001568354A4F44-45E3DE3E', 1, 'Tequila (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190437-000015686A97C9C6-EB5C0F63', '20081130190436-00001568354A4F44-45E3DE3E', 2, 'Likier Cointreau (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190437-000015686AC5E839-34504892', '20081130190436-00001568354A4F44-45E3DE3E', 3, 'Coca-cola')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190437-000015686B0BDE25-623E561F', '20081130190436-00001568354A4F44-45E3DE3E', 4, 'Cytryna (plasterek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130190437-000015686B2799F3-C3D90A46', '20081130190436-00001568354A4F44-45E3DE3E', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191516-000015FD1D1C3D54-89ADC12E', '20081130191508-000015FB5CA48547-665DB1E3', 1, 'Bia³y rum Bacardi (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191516-000015FD1D415C1C-E2D682AE', '20081130191508-000015FB5CA48547-665DB1E3', 2, 'Likier bananowy Pisang Ambon (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191516-000015FD1D609F7B-37FA8F7B', '20081130191508-000015FB5CA48547-665DB1E3', 3, 'Sok bananowy (50ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191516-000015FD1D7EF2C7-0CCD89C3', '20081130191508-000015FB5CA48547-665DB1E3', 4, 'Œwie¿y sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191516-000015FD1D9CAB1C-5D4935FE', '20081130191508-000015FB5CA48547-665DB1E3', 5, 'Limonka (1/2szt.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191516-000015FD1E1DA14D-6A72A272', '20081130191508-000015FB5CA48547-665DB1E3', 6, 'Miêta (1 ga³¹zka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191516-000015FD1E408559-6D4554E9', '20081130191508-000015FB5CA48547-665DB1E3', 7, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191536-00001601C7F9F39D-32BC4CA1', '20081130191534-00001601734B4903-80A66C05', 1, 'Bia³y rum Bacardi (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191536-00001601C81B0C3C-EACA0270', '20081130191534-00001601734B4903-80A66C05', 2, 'Likier Curacao Triple Sec (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191536-00001601C8385542-AB4F295D', '20081130191534-00001601734B4903-80A66C05', 3, 'Likier bananowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191536-00001601C85516F7-FDB3F017', '20081130191534-00001601734B4903-80A66C05', 4, 'Banan (1szt.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191536-00001601C871C3F0-67E5B863', '20081130191534-00001601734B4903-80A66C05', 5, 'Sok cytrynowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191536-00001601C88E5144-71FD7D8B', '20081130191534-00001601734B4903-80A66C05', 6, 'T³uczony lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191549-00001604CADA4A2D-B7B6DED6', '20081130191547-000016044A6C13C7-B8114B16', 1, 'Likier bananowy (15 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191549-00001604CB0E7EF6-9D7D28DF', '20081130191547-000016044A6C13C7-B8114B16', 2, 'Advocat (15 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191549-00001604CB2EEA11-EE43797F', '20081130191547-000016044A6C13C7-B8114B16', 3, 'Likier Jägermeister (10 ml) (obejdzie siê bez)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191549-00001604CB4F4FB8-78074B19', '20081130191547-000016044A6C13C7-B8114B16', 4, 'Mleko zagêszczone.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191556-000016069383C660-AF643D56', '20081130191556-000016065CA91BB2-D7BD9CE7', 1, 'Wytrawny Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191556-0000160693A49D84-FA7D317D', '20081130191556-000016065CA91BB2-D7BD9CE7', 2, 'Bia³y rum Bacardi (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191556-0000160693C461A9-BC20275F', '20081130191556-000016065CA91BB2-D7BD9CE7', 3, 'Apricot Brandy (5 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191556-0000160693E3049E-B384E170', '20081130191556-000016065CA91BB2-D7BD9CE7', 4, 'Amaretto di Saronno (5 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191556-00001606940059A6-48A7B4B7', '20081130191556-000016065CA91BB2-D7BD9CE7', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191605-00001608A653C1A4-4A53FC23', '20081130191604-000016085E8CCB76-0A164D7D', 1, 'Likier Cointreau (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191605-00001608A6757BC5-D811004B', '20081130191604-000016085E8CCB76-0A164D7D', 2, 'Scotch Whisky (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191605-00001608A6937365-3EB0722A', '20081130191604-000016085E8CCB76-0A164D7D', 3, 'Likier Kahlua (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191605-00001608A6B1E99C-1D194B78', '20081130191604-000016085E8CCB76-0A164D7D', 4, 'Sok z 1/4 cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191605-00001608A6CF741D-08ED469C', '20081130191604-000016085E8CCB76-0A164D7D', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191620-0000160BF828E99D-FEF272A6', '20081130191618-0000160B8217370D-10138002', 1, 'Szampan (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191620-0000160BF8495916-E57B15BB', '20081130191618-0000160B8217370D-10138002', 2, 'Likier Tia Maria (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191620-0000160BF887A945-5438000A', '20081130191618-0000160B8217370D-10138002', 3, 'Brandy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191620-0000160BF8A72E1F-1BF135D1', '20081130191618-0000160B8217370D-10138002', 4, 'ciemne wiœnie koktajlowe.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191628-0000160DF1D41B36-4EAC5B3F', '20081130191627-0000160DA197D49F-A804DC88', 1, 'Wódka czysta (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191628-0000160DF1F4A082-176C585F', '20081130191627-0000160DA197D49F-A804DC88', 2, 'Likier kawowy (najlepiej Kahlua) - (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191628-0000160DF217525B-280DC1DE', '20081130191627-0000160DA197D49F-A804DC88', 3, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191649-00001612D36E0198-27E574C1', '20081130191648-000016127C18E81A-834DABA6', 1, 'Likieru Tia Maria (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191649-00001612D38E62E1-7D66139C', '20081130191648-000016127C18E81A-834DABA6', 2, 'Wódka (1miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191649-00001612D3AAF8F0-7BAC2302', '20081130191648-000016127C18E81A-834DABA6', 3, 'Coca-Cola')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191649-00001612D3C76012-0B4706CF', '20081130191648-000016127C18E81A-834DABA6', 4, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191658-00001614E3CE0DDB-25D44549', '20081130191657-000016149BF54FFD-AEBD4E04', 1, 'Likier Parfait Amour (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191658-00001614E3EF916C-2C9ED245', '20081130191657-000016149BF54FFD-AEBD4E04', 2, 'Likier Blue Curacao (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191658-00001614E430BBAA-BC37F2D9', '20081130191657-000016149BF54FFD-AEBD4E04', 3, 'Kirschwasser (wódka wiœniowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191658-00001614E44F7C16-AC884D9A', '20081130191657-000016149BF54FFD-AEBD4E04', 4, 'wytrawna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191658-00001614E46E1768-51110D1E', '20081130191657-000016149BF54FFD-AEBD4E04', 5, 'bezbarwna) - (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191658-00001614E48BB246-1986429C', '20081130191657-000016149BF54FFD-AEBD4E04', 6, 'S³odka œmietanka (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191658-00001614E4A81CAE-B6E0E7AB', '20081130191657-000016149BF54FFD-AEBD4E04', 7, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191706-00001616B06E4ADF-401203B2', '20081130191705-0000161678B4C03C-BAE7D6FC', 1, 'Likier Curacao Blue (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191706-00001616B08E91F8-84C91449', '20081130191705-0000161678B4C03C-BAE7D6FC', 2, 'Wódka czysta (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191706-00001616B0AE1D5E-748944C1', '20081130191705-0000161678B4C03C-BAE7D6FC', 3, 'Lemoniada')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191706-00001616B0D06216-515065F4', '20081130191705-0000161678B4C03C-BAE7D6FC', 4, 'plasterek cytryny.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191714-00001618BBCB985B-9D3F4068', '20081130191712-0000161844E8E2A4-DF0AC4B0', 1, 'Cointreau (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191714-00001618BBEAA988-D5A57E37', '20081130191712-0000161844E8E2A4-DF0AC4B0', 2, 'Curacao Blue (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191714-00001618BC05E8ED-BE1AAA84', '20081130191712-0000161844E8E2A4-DF0AC4B0', 3, 'Sok ananasowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191714-00001618BC204E12-DD12FA3C', '20081130191712-0000161844E8E2A4-DF0AC4B0', 4, 'Piwo imbirowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191714-00001618BC3B61D1-F7A407FF', '20081130191712-0000161844E8E2A4-DF0AC4B0', 5, 'Lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191724-0000161ADA87559F-0743C0D2', '20081130191722-0000161A84CBC6CD-C88829C1', 1, 'Scotch Whisky (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191724-0000161ADAA7B173-CFAEB2CB', '20081130191722-0000161A84CBC6CD-C88829C1', 2, 'Czerwony Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191724-0000161ADAC2E07B-B1A36E67', '20081130191722-0000161A84CBC6CD-C88829C1', 3, 'Wytrawny Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191724-0000161ADADCF93C-A3928099', '20081130191722-0000161A84CBC6CD-C88829C1', 4, 'Likier Benedictine (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191724-0000161ADAF758EC-E22D1B72', '20081130191722-0000161A84CBC6CD-C88829C1', 5, 'Skórka z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191724-0000161ADB115894-D0DA3D90', '20081130191722-0000161A84CBC6CD-C88829C1', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191732-0000161CE0C4F926-D2134301', '20081130191731-0000161C8E71C0E8-EDAFA489', 1, 'Jameson Irish Whisky (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191732-0000161CE0E7AE45-C99BB8CF', '20081130191731-0000161C8E71C0E8-EDAFA489', 2, 'Wódka any¿owa Pastis (1/2 barowej ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191732-0000161CE1031DAE-A5F82960', '20081130191731-0000161C8E71C0E8-EDAFA489', 3, 'Likier pomarañczowy (1/2 barowej ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191732-0000161CE11F47B4-9847E475', '20081130191731-0000161C8E71C0E8-EDAFA489', 4, 'Likier Maraschino (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191732-0000161CE144561D-001869D3', '20081130191731-0000161C8E71C0E8-EDAFA489', 5, 'Angostury Bitters (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191732-0000161CE165994B-70E40915', '20081130191731-0000161C8E71C0E8-EDAFA489', 6, 'Pomarañcz (1/2 plastra)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191732-0000161CE180B90B-D2133330', '20081130191731-0000161C8E71C0E8-EDAFA489', 7, '1 zielona oliwka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191732-0000161CE19AD3FB-6A924085', '20081130191731-0000161C8E71C0E8-EDAFA489', 8, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191740-0000161E98E95D7D-4153328A', '20081130191738-0000161E511FDE01-0B398DE0', 1, 'Pó³wytrawne wino musuj¹ce')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191740-0000161E990827BB-C933C31C', '20081130191738-0000161E511FDE01-0B398DE0', 2, 'Likier Drumbuie (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191740-0000161E9922E316-8C1CFC7A', '20081130191738-0000161E511FDE01-0B398DE0', 3, 'Œwierzy sok pomarañczowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191740-0000161E993CBEBB-4DEB30BE', '20081130191738-0000161E511FDE01-0B398DE0', 4, 'Skórka z 1/2 pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191740-0000161E995693D3-AC8CA0A3', '20081130191738-0000161E511FDE01-0B398DE0', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191748-00001620A66F76DD-929F3A5B', '20081130191747-00001620697C73B8-9CFAF3C3', 1, 'Brandy (1 1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191748-00001620A68E8921-55D3F9B3', '20081130191747-00001620697C73B8-9CFAF3C3', 2, 'Sok cytrynowy (1 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191748-00001620A6A85E3A-194BB499', '20081130191747-00001620697C73B8-9CFAF3C3', 3, 'Creme de Cassis (1 dash)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191748-00001620A6C1F97C-99B49749', '20081130191747-00001620697C73B8-9CFAF3C3', 4, 'Cytryna (plasterek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191748-00001620A6DB8460-753C81F1', '20081130191747-00001620697C73B8-9CFAF3C3', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191756-0000162273C0B280-8100CB76', '20081130191755-000016222CF9F9A6-4F248497', 1, 'Brandy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191756-0000162273DDA896-F2337D4C', '20081130191755-000016222CF9F9A6-4F248497', 2, 'Likier Maraschino (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191756-0000162273FFE14E-DD270C94', '20081130191755-000016222CF9F9A6-4F248497', 3, 'Bia³e Curacao (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191756-00001622741C35E2-7E4C0842', '20081130191755-000016222CF9F9A6-4F248497', 4, 'Angostury Bitter (kilka kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191756-000016227435D124-66404E8F', '20081130191755-000016222CF9F9A6-4F248497', 5, 'Cukier (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191756-00001622744EE515-EFF35DE8', '20081130191755-000016222CF9F9A6-4F248497', 6, 'Sok z 1/2 cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191756-000016227468350B-52039688', '20081130191755-000016222CF9F9A6-4F248497', 7, 'Cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191756-000016227480E268-50D9C316', '20081130191755-000016222CF9F9A6-4F248497', 8, 'Sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191756-00001622749A0C2C-D9995C59', '20081130191755-000016222CF9F9A6-4F248497', 9, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191942-0000163B18AA7F32-61C40475', '20081130191940-0000163ABE20508E-1582C62D', 1, 'Wódka ¯o³¹dkowa Gorzka z Miodem (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191942-0000163B195933CE-3151256B', '20081130191940-0000163ABE20508E-1582C62D', 2, 'CREME by ¯o³¹dkowa Gorzka (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191942-0000163B1977D266-2D7F01EA', '20081130191940-0000163ABE20508E-1582C62D', 3, 'Mleko (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191942-0000163B199161A7-C05B6ACA', '20081130191940-0000163ABE20508E-1582C62D', 4, 'Napar z kawy (ekspress) (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191942-0000163B1A0A5213-34B1D112', '20081130191940-0000163ABE20508E-1582C62D', 5, 'Syrop waniliowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191942-0000163B1A71CA78-325263BC', '20081130191940-0000163ABE20508E-1582C62D', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191952-0000163D69BCBB71-4B0BE7D3', '20081130191948-0000163C8E0C2A15-1796B2D1', 1, 'Campari (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191952-0000163D69D9291F-7FF54CDF', '20081130191948-0000163C8E0C2A15-1796B2D1', 2, 'Cointreau (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191952-0000163D69F1ABEC-62EB70D3', '20081130191948-0000163C8E0C2A15-1796B2D1', 3, 'Sok pomarañczowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191952-0000163D6A853E99-C80B5B27', '20081130191948-0000163C8E0C2A15-1796B2D1', 4, 'Sok grejpfrutowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191952-0000163D6ABAF03C-417F07CD', '20081130191948-0000163C8E0C2A15-1796B2D1', 5, 'Sok z cytryny (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191952-0000163D6AD59E7F-9645710E', '20081130191948-0000163C8E0C2A15-1796B2D1', 6, 'Pomarañcz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191952-0000163D6B9402FA-2618A2BB', '20081130191948-0000163C8E0C2A15-1796B2D1', 7, 'Ogórek do dekoracji')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130191952-0000163D6BAFAA0C-3CA5F635', '20081130191948-0000163C8E0C2A15-1796B2D1', 8, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192005-00001640558F6F75-BCA98758', '20081130192003-0000163FF61B9A58-05A103EB', 1, 'Canadian Club Whisky (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192005-0000164055B0AB00-84BBFA96', '20081130192003-0000163FF61B9A58-05A103EB', 2, 'Likier Drambuie (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192005-0000164055CA618A-1A890FA2', '20081130192003-0000163FF61B9A58-05A103EB', 3, 'Œwie¿y sok pomarañczowy (60 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192005-000016405672B36D-21925107', '20081130192003-0000163FF61B9A58-05A103EB', 4, 'Sok cytrynowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192005-00001640568F2461-E7857C86', '20081130192003-0000163FF61B9A58-05A103EB', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192013-000016426749C2DB-4EB06BCF', '20081130192012-000016421CBECCBC-2D6F7343', 1, 'likier Southern Comfort (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192013-0000164267915547-ECF53003', '20081130192012-000016421CBECCBC-2D6F7343', 2, 'Bourbon Whisky (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192013-0000164267AE312D-B1075355', '20081130192012-000016421CBECCBC-2D6F7343', 3, 'Amaretto di Saronno (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192013-0000164268758B82-308E9E06', '20081130192012-000016421CBECCBC-2D6F7343', 4, 'Œwie¿y sok pomarañczowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192013-0000164268932661-B7FB1A37', '20081130192012-000016421CBECCBC-2D6F7343', 5, 'Sok cytrynowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192013-0000164269597213-A1CA9A98', '20081130192012-000016421CBECCBC-2D6F7343', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192028-00001645E2B1DC3B-227688E3', '20081130192027-000016459007A671-A4E1C279', 1, 'Bia³y rum Bacardi (1/2 miarki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192028-00001645E2D0143E-7E331673', '20081130192027-000016459007A671-A4E1C279', 2, 'Likier bananowy (1/2 miarki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192028-00001645E2E92600-7AF5385C', '20081130192027-000016459007A671-A4E1C279', 3, 'Szampan Bollinger')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192028-00001645E300DDD4-6458496D', '20081130192027-000016459007A671-A4E1C279', 4, 'Banan (1 plaster).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192037-00001647F6C786B5-7B687238', '20081130192036-00001647A1441100-E27EC731', 1, 'Bia³y Rum Bacardi (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192037-00001647F766699D-FE8A3279', '20081130192036-00001647A1441100-E27EC731', 2, 'Rum 73% (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192037-00001647F780E495-82C7A599', '20081130192036-00001647A1441100-E27EC731', 3, 'Likier z maracuji (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192037-00001647F798691E-B75C523E', '20081130192036-00001647A1441100-E27EC731', 4, 'Likier kokosowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192037-00001647F86AF0C2-3D99D3B4', '20081130192036-00001647A1441100-E27EC731', 5, 'Angostura Bitters ( kilka kropel)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192037-00001647F88442E7-538D5800', '20081130192036-00001647A1441100-E27EC731', 6, 'Orange Bitters (kilka kropel)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192037-00001647F89B84DF-A7251D7A', '20081130192036-00001647A1441100-E27EC731', 7, 'Syrop grenadynowy (1/2 barowej ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192037-00001647F90BE834-E1B1C483', '20081130192036-00001647A1441100-E27EC731', 8, 'Sok ananasowy (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192037-00001647F969F2B6-A642FE83', '20081130192036-00001647A1441100-E27EC731', 9, 'Sok pomarañczowy (10')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192046-00001649FC5B18ED-F18873C3', '20081130192045-00001649BE46F894-ACF78506', 1, 'Z³oty rum (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192046-00001649FC7A33EC-2EF8DBB9', '20081130192045-00001649BE46F894-ACF78506', 2, 'Likier Curacao (2 ³y¿ki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192046-00001649FC9287CD-DDC2FC2B', '20081130192045-00001649BE46F894-ACF78506', 3, 'Likier Maraschino (2 ³y¿ki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192046-00001649FCFBED73-ADB69A9A', '20081130192045-00001649BE46F894-ACF78506', 4, 'Sok z cytryny (1 ³y¿ka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192046-00001649FD3F1AC6-6126FB5D', '20081130192045-00001649BE46F894-ACF78506', 5, 'Angostury Bitter (2 krople).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192100-0000164D46FFB0B6-F57ED08A', '20081130192059-0000164CF8DFA83A-1D458BEA', 1, 'Likier pomarañczowy (1/2 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192100-0000164D47330D6D-0C6FD125', '20081130192059-0000164CF8DFA83A-1D458BEA', 2, 'Lkier Maraschino (1/2 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192100-0000164D474F1F70-7BB356F6', '20081130192059-0000164CF8DFA83A-1D458BEA', 3, 'Cognac Hine (1/2 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192100-0000164D4766FE8C-1CBB0E7F', '20081130192059-0000164CF8DFA83A-1D458BEA', 4, 'szampan Bolliner')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192100-0000164D4832F6BA-98ED30DB', '20081130192059-0000164CF8DFA83A-1D458BEA', 5, 'T³uczony lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192109-0000164F49407D16-4C7DE1C8', '20081130192107-0000164EF141FA58-A6699114', 1, 'Malibu (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192109-0000164F495CAB79-41552D3C', '20081130192107-0000164EF141FA58-A6699114', 2, 'Likier Cointreau (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192109-0000164F49748638-216E4738', '20081130192107-0000164EF141FA58-A6699114', 3, 'Sok z limonki (1/2 szt.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192109-0000164F498B476B-75FA7025', '20081130192107-0000164EF141FA58-A6699114', 4, 'Sprite')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192109-0000164F4A06BCC8-D04833CE', '20081130192107-0000164EF141FA58-A6699114', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192120-00001651E50222E7-E49F28D2', '20081130192119-000016519A86772B-D40C1911', 1, 'Likier Cointreau (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192120-00001651E51E6091-9D8A7E3C', '20081130192119-000016519A86772B-D40C1911', 2, 'Limetka (1/2)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192120-00001651E5DB002C-0C9A19A6', '20081130192119-000016519A86772B-D40C1911', 3, 'Kruszony lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192128-00001653B255CDA7-97D6EFE6', '20081130192126-0000165369636DC3-31C4AAB3', 1, 'Likier Cointreau (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192128-00001653B2B030F1-04FF48FF', '20081130192126-0000165369636DC3-31C4AAB3', 2, 'Sok z limetki lub cytryny (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192128-00001653B2D9EA4B-DD1B70D8', '20081130192126-0000165369636DC3-31C4AAB3', 3, 'Sok grapefruitowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192128-00001653B2F3FD97-9F5719AA', '20081130192126-0000165369636DC3-31C4AAB3', 4, 'Limetka lub cytryna (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192128-00001653B30B567A-5170C56D', '20081130192126-0000165369636DC3-31C4AAB3', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192136-0000165594367742-39253DA0', '20081130192135-0000165549EDAE04-5D59EB5C', 1, 'Likier Cointreau (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192136-0000165594664A38-F1286CBD', '20081130192135-0000165549EDAE04-5D59EB5C', 2, 'Bia³e wino.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192145-00001657B2F30724-AF01B5C6', '20081130192143-000016574C192495-5DB90677', 1, 'Likier Cointreau (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192145-00001657B34E8CB5-55D92C9E', '20081130192143-000016574C192495-5DB90677', 2, 'Tonik')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192145-00001657B3691068-273EBDF6', '20081130192143-000016574C192495-5DB90677', 3, 'Cytryna (plasterek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192145-00001657B380D43D-26624127', '20081130192143-000016574C192495-5DB90677', 4, 'Lód (kostki).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192210-0000165D9BECBC93-D6EFD6EB', '20081130192209-0000165D54470AAA-740C131F', 1, 'Wódka ¯o³¹dkowa Gorzka z Miodem (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192210-0000165D9C0954D0-4A4A6CE0', '20081130192209-0000165D54470AAA-740C131F', 2, 'CREME by ¯o³¹dkowa Gorzka (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192210-0000165D9C21DAE3-CD42962D', '20081130192209-0000165D54470AAA-740C131F', 3, 'Mleko (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192210-0000165D9C9B6645-26215748', '20081130192209-0000165D54470AAA-740C131F', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192223-00001660991D6A15-2A4D585F', '20081130192222-000016603E8AF431-0D83231F', 1, 'Tequili Gold (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192223-0000166099535B03-9FF8CED3', '20081130192222-000016603E8AF431-0D83231F', 2, 'Likier Blue Curacao (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192223-00001660996E26BC-88F11A8C', '20081130192222-000016603E8AF431-0D83231F', 3, 'Sok z cytryny (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192223-0000166099864BB0-EB56540C', '20081130192222-000016603E8AF431-0D83231F', 4, 'Woda sodowa (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192223-00001660999E5216-9E924EDB', '20081130192222-000016603E8AF431-0D83231F', 5, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192223-0000166099B5AE3E-AFF4C36C', '20081130192222-000016603E8AF431-0D83231F', 6, 'Cytryna (1 plasterek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192233-00001662E41E9B35-615218BB', '20081130192232-000016629FDB9E4F-4CA01BE7', 1, 'Bia³y rum Bacardi (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192233-00001662E46E5CDE-773419A3', '20081130192232-000016629FDB9E4F-4CA01BE7', 2, 'Cukier puder (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192233-00001662E4C397B8-C3DA20F7', '20081130192232-000016629FDB9E4F-4CA01BE7', 3, 'Sok cytrynowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192233-00001662E4E8B222-010DE483', '20081130192232-000016629FDB9E4F-4CA01BE7', 4, 'Sok grejpfrutowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192233-00001662E5024164-60BD7B53', '20081130192232-000016629FDB9E4F-4CA01BE7', 5, 'Likier Maraschino (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192233-00001662E519ABBC-CB7E2599', '20081130192232-000016629FDB9E4F-4CA01BE7', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192241-00001664B8B81EDB-9000CB47', '20081130192240-000016647FCBEE9F-157A38FF', 1, 'Szampan (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192241-00001664B8D4D378-E32812A3', '20081130192240-000016647FCBEE9F-157A38FF', 2, '¯ó³ty likier Chartreuse (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192241-00001664B8ECB921-0EC7C561', '20081130192240-000016647FCBEE9F-157A38FF', 3, 'Syrop grenadine (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192241-00001664B91C2699-ED864202', '20081130192240-000016647FCBEE9F-157A38FF', 4, 'Sok cytrynowy (20 ml).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192247-000016663B4121BC-E69B08E3', '20081130192246-00001665FAEDA450-120A5ED5', 1, 'Likier Cointreau (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192247-000016663B5CAB56-EFA7C526', '20081130192246-00001665FAEDA450-120A5ED5', 2, 'Wódka (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192247-000016663B8658AF-186F2B3D', '20081130192246-00001665FAEDA450-120A5ED5', 3, 'Tonik')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192247-000016663BD4F811-3C6691E5', '20081130192246-00001665FAEDA450-120A5ED5', 4, 'Skórka pomarañczowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192247-000016663BF1BE24-3C6DE992', '20081130192246-00001665FAEDA450-120A5ED5', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192256-000016685161429F-FA42CDEE', '20081130192255-000016680E97425E-15C134AB', 1, 'Koniak (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192256-00001668518AE626-091899BA', '20081130192255-000016680E97425E-15C134AB', 2, 'Œmietanka (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192256-0000166851A61873-CD80422E', '20081130192255-000016680E97425E-15C134AB', 3, 'Likier kawowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192256-0000166851BE390A-C7DBBB8C', '20081130192255-000016680E97425E-15C134AB', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192305-0000166A468389EF-03609569', '20081130192304-0000166A063A6964-ED8B3667', 1, 'Likier miêtowy Dlaczego Nie (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192305-0000166A46A09B4E-A90C62BB', '20081130192304-0000166A063A6964-ED8B3667', 2, 'Gin Lubuski (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192305-0000166A46B8CB2C-2A7D534C', '20081130192304-0000166A063A6964-ED8B3667', 3, 'Wódka (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192305-0000166A46D06471-96780680', '20081130192304-0000166A063A6964-ED8B3667', 4, 'Sok z cytryny (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192305-0000166A46E7C726-E061B0AD', '20081130192304-0000166A063A6964-ED8B3667', 5, 'Woda mineralna (70 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192305-0000166A46FF4A98-511A7B70', '20081130192304-0000166A063A6964-ED8B3667', 6, 'Miêta (ga³azka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192305-0000166A4725F352-D643CD4B', '20081130192304-0000166A063A6964-ED8B3667', 7, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192313-0000166C2094FC78-0DA11B4C', '20081130192312-0000166BDFAF34D8-BDC6B1AC', 1, 'Wytrawny Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192313-0000166C20B3058E-8A0EDEDC', '20081130192312-0000166BDFAF34D8-BDC6B1AC', 2, 'Likier Drambuie (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192313-0000166C20CAB7ED-6E3AD541', '20081130192312-0000166BDFAF34D8-BDC6B1AC', 3, 'Gordon''s Dry Gin (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192313-0000166C20E2175C-6F8F36C4', '20081130192312-0000166BDFAF34D8-BDC6B1AC', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192322-0000166E3A59AC4F-9D318ADB', '20081130192321-0000166DFEC9C33B-80F37713', 1, 'Likier Curacao Triple Sec (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192322-0000166E3A7743E8-C0C1FFF1', '20081130192321-0000166DFEC9C33B-80F37713', 2, 'Sok pomarañczowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192322-0000166E3AB0445E-1B56B7A9', '20081130192321-0000166DFEC9C33B-80F37713', 3, 'Sok cytrynowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192322-0000166E3B050DBA-3AB7A315', '20081130192321-0000166DFEC9C33B-80F37713', 4, 'Likier Maraschino (1/2 barowej ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192322-0000166E3B1E077A-185DF10D', '20081130192321-0000166DFEC9C33B-80F37713', 5, 'Jajko (1szt.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192322-0000166E3B354EE7-237E0D7E', '20081130192321-0000166DFEC9C33B-80F37713', 6, 'Szampan Bollinger')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192322-0000166E3B4C4E4E-D0862F93', '20081130192321-0000166DFEC9C33B-80F37713', 7, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192331-0000167052CAFBC8-A438AE8F', '20081130192330-0000167011E9B25B-2AB59024', 1, 'Wódka (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192331-0000167052E8C7C2-A6778E68', '20081130192330-0000167011E9B25B-2AB59024', 2, 'Likier malinowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192331-0000167053344CB2-EF4D538D', '20081130192330-0000167011E9B25B-2AB59024', 3, 'Sok grejpfrutowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192331-00001670534E6D17-5EBD73B6', '20081130192330-0000167011E9B25B-2AB59024', 4, 'Gazowana woda mineralna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192331-0000167053660F17-6991570A', '20081130192330-0000167011E9B25B-2AB59024', 5, 'Cytryna do dekoracji.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192338-000016721E839418-27B70E1B', '20081130192337-00001671C918D363-ADF46B1D', 1, 'Likier Drambuie (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192338-000016721EA003F5-747E54F4', '20081130192337-00001671C918D363-ADF46B1D', 2, 'Wódka Finlandia (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192338-000016721EB78DF3-656FF859', '20081130192337-00001671C918D363-ADF46B1D', 3, 'Sok ananasowy (nie s³odzony) - (80 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192338-000016721ECE92CF-4FC80025', '20081130192337-00001671C918D363-ADF46B1D', 4, 'Sok cytrynowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192338-000016721EE58863-466C7C2D', '20081130192337-00001671C918D363-ADF46B1D', 5, 'Kawa³ek œwierzego ananasa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192338-000016721EFCCEB9-103F0F9D', '20081130192337-00001671C918D363-ADF46B1D', 6, 'Miêta (1 ga³¹zka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192338-000016721F365ADE-83577D4A', '20081130192337-00001671C918D363-ADF46B1D', 7, '1 wiœnia koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192338-000016721F58255D-A1380E47', '20081130192337-00001671C918D363-ADF46B1D', 8, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192347-000016741ECCD74B-D54AA200', '20081130192345-00001673BE3AB41F-8E427E1F', 1, 'Wódka (60ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192347-000016741EE9C390-8795A5F2', '20081130192345-00001673BE3AB41F-8E427E1F', 2, 'Grenadine (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192347-000016741F0172A8-58D6730D', '20081130192345-00001673BE3AB41F-8E427E1F', 3, 'Likier Blue Curacao (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192347-000016741F1C0145-1FC47C64', '20081130192345-00001673BE3AB41F-8E427E1F', 4, 'Malibu (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192347-000016741F3482FB-BBAB1AF7', '20081130192345-00001673BE3AB41F-8E427E1F', 5, 'Likier Amaretto (25ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192347-000016741F82AD99-94CACB15', '20081130192345-00001673BE3AB41F-8E427E1F', 6, 'Sok z czarnej porzeczki (80ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192347-000016741F9D6E68-1D045B3D', '20081130192345-00001673BE3AB41F-8E427E1F', 7, 'Nektar ananasowy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192347-000016741FB55986-006B358A', '20081130192345-00001673BE3AB41F-8E427E1F', 8, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192413-0000167A1E53E580-98255695', '20081130192411-00001679C6380846-1867677C', 1, 'Bia³y wermut (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192413-0000167A1E701958-758A96F7', '20081130192411-00001679C6380846-1867677C', 2, 'Likier Amaretto di Saronno (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192413-0000167A1E87C0CD-C1A485CF', '20081130192411-00001679C6380846-1867677C', 3, 'Starta skórka cytryny (1/3 ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192413-0000167A1E9FD562-309D122D', '20081130192411-00001679C6380846-1867677C', 4, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192421-0000167C1E000420-E8427B81', '20081130192420-0000167BE550B797-2A9F4F85', 1, 'Wódka Finlandia Redberry (25ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192421-0000167C1E1D18C5-C6763880', '20081130192420-0000167BE550B797-2A9F4F85', 2, 'Likier Passoa (25ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192421-0000167C1E3E04A5-36756D2B', '20081130192420-0000167BE550B797-2A9F4F85', 3, 'Sok pomarañczowy (50ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192421-0000167C1E80BD34-4FACA96D', '20081130192420-0000167BE550B797-2A9F4F85', 4, 'Sok z czerwonego grejfruta (25ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192421-0000167C1E9B63D3-971FBB18', '20081130192420-0000167BE550B797-2A9F4F85', 5, 'Pomarañcz (3 plastry).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192430-0000167E2E1EF33D-E46E8207', '20081130192429-0000167DE3967FE0-E6BAE738', 1, 'Amaretto di Saronno (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192430-0000167E2E3AA64F-4E99E64A', '20081130192429-0000167DE3967FE0-E6BAE738', 2, 'Cognac Hine (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192430-0000167E2E52A740-E3BC36F0', '20081130192429-0000167DE3967FE0-E6BAE738', 3, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192440-000016806557D913-9FA40C45', '20081130192439-0000168022BC2749-B3BA6A76', 1, 'Tequila (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192440-0000168065749324-C2CBBB89', '20081130192439-0000168022BC2749-B3BA6A76', 2, 'Likier pomarañczowy (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192440-0000168065B4B893-E46DDED2', '20081130192439-0000168022BC2749-B3BA6A76', 3, 'Sok cytrynowy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192440-0000168065D172A4-2A471073', '20081130192439-0000168022BC2749-B3BA6A76', 4, 'Cukier puder (2 barowe ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192440-0000168065EA35D4-AEAC447B', '20081130192439-0000168022BC2749-B3BA6A76', 5, 'Sól')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192440-000016806601A6B9-CD20BA7D', '20081130192439-0000168022BC2749-B3BA6A76', 6, 'Sok cytrynowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192440-000016806618E453-7F47ADB0', '20081130192439-0000168022BC2749-B3BA6A76', 7, 'T³uczony lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192450-00001682CAE402EC-DB523356', '20081130192449-000016828FF2144F-A822F4D4', 1, 'Whisky Dewar’s 12 YO (60 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192450-00001682CB00B671-C66DD91C', '20081130192449-000016828FF2144F-A822F4D4', 2, 'Likieru Amaretto (25 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192450-00001682CB18D81F-4B1ABAD5', '20081130192449-000016828FF2144F-A822F4D4', 3, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192458-00001684A420107B-594C5D32', '20081130192457-000016845940ADC2-6EF156BE', 1, 'Bia³y Creme de Cacao (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192458-00001684A490C5A8-BB0612DB', '20081130192457-000016845940ADC2-6EF156BE', 2, 'Likier Galliano (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192458-00001684A4ABC6DA-9000DE46', '20081130192457-000016845940ADC2-6EF156BE', 3, 'S³odka œmietanka (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192458-00001684A4C3DB70-955B748C', '20081130192457-000016845940ADC2-6EF156BE', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192506-0000168678BC0227-28CBEF27', '20081130192505-000016863E511CCA-6F6B9799', 1, 'Bia³y Creme de Cacao (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192506-0000168678D80941-AB55C104', '20081130192505-000016863E511CCA-6F6B9799', 2, 'Zielony Creme de Menthe (15 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192506-0000168679038BD8-1ACC5CFC', '20081130192505-000016863E511CCA-6F6B9799', 3, 'S³odka œmietanka (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192506-00001686791E7966-68666716', '20081130192505-000016863E511CCA-6F6B9799', 4, 'Miêta (1 ga³¹zka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192506-000016867938A7FA-7C6486B5', '20081130192505-000016863E511CCA-6F6B9799', 5, 'Lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192513-0000168811CA2C62-62E16696', '20081130192512-00001687D1FECB36-CDA5B9C4', 1, 'Likier bananowy Pisang Ambon (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192513-0000168811E6CA14-5652D77D', '20081130192512-00001687D1FECB36-CDA5B9C4', 2, 'Krem kokosowy (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192513-0000168811FE912E-95BCEB6C', '20081130192512-00001687D1FECB36-CDA5B9C4', 3, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192521-00001689F0F42A73-0AAE4757', '20081130192520-00001689B674E92E-42649FE6', 1, 'Likier bananowy Pisang Ambon (60 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192521-00001689F111388C-48CAE233', '20081130192520-00001689B674E92E-42649FE6', 2, 'Bia³y rum Bacardi (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192521-00001689F1296523-2F99C4C4', '20081130192520-00001689B674E92E-42649FE6', 3, 'Sok z limetki (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192521-00001689F1405DFE-B83A2A0E', '20081130192520-00001689B674E92E-42649FE6', 4, 'Limetka (1 plaster)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192521-0000168A002AD82D-42A46CFD', '20081130192520-00001689B674E92E-42649FE6', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192529-0000168BD41DF3EB-5D8721BB', '20081130192528-0000168B9E479000-F1468549', 1, 'Gin (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192529-0000168BD43B643B-18BA5D87', '20081130192528-0000168B9E479000-F1468549', 2, 'Creme de Menthe (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192529-0000168BD4531E3D-B43864FD', '20081130192528-0000168B9E479000-F1468549', 3, 'Sok z limy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192529-0000168BD46A98F4-832DA7B6', '20081130192528-0000168B9E479000-F1468549', 4, '1 ga³¹zka miêty')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192529-0000168BD481FEEF-EBD6115F', '20081130192528-0000168B9E479000-F1468549', 5, 'Lód (kika kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192536-0000168D98D4A516-15DEE462', '20081130192535-0000168D502C6ABE-4264DBE3', 1, 'Wino musuj¹ce')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192536-0000168D98F0CBD5-7C751FA3', '20081130192535-0000168D502C6ABE-4264DBE3', 2, 'Likier Amaretto di Saronno (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192536-0000168D9908AC09-526E5BEA', '20081130192535-0000168D502C6ABE-4264DBE3', 3, 'Gordon''s Dry Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192536-0000168D9923AD3B-B35157F1', '20081130192535-0000168D502C6ABE-4264DBE3', 4, 'Niebieskie Curacao (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192536-0000168D993C4296-4864AB64', '20081130192535-0000168D502C6ABE-4264DBE3', 5, 'Œwierzy sok pomarañczowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192536-0000168D99933CDC-6D3D89F2', '20081130192535-0000168D502C6ABE-4264DBE3', 6, '1 bia³ko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192536-0000168D99AE426B-3E42F887', '20081130192535-0000168D502C6ABE-4264DBE3', 7, 'Miêta (1 ga³¹zka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192536-0000168D99C5EE3E-BC2BE9F7', '20081130192535-0000168D502C6ABE-4264DBE3', 8, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192545-0000168F8499A22C-2B88BECA', '20081130192543-0000168F32C1C51A-F0B55B3F', 1, 'Wódka Wyborowa (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192545-0000168F84B73338-1CA9C2FA', '20081130192543-0000168F32C1C51A-F0B55B3F', 2, 'Likier Blue Curacao (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192545-0000168F84CFFE0C-928C5E13', '20081130192543-0000168F32C1C51A-F0B55B3F', 3, 'Sok pomarañczowy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192545-0000168F84E70BA2-ED3B8F0A', '20081130192543-0000168F32C1C51A-F0B55B3F', 4, 'Lód (3-4 kostki).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192821-000016B3E70ECBD3-A1557387', '20081130192819-000016B362EBA05C-53883D9D', 1, 'Rum Bacardi (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192821-000016B3E73FDD6F-EE9AFCD7', '20081130192819-000016B362EBA05C-53883D9D', 2, 'Likier Apricot Brandy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192821-000016B3E7B4E2FA-06E0CC10', '20081130192819-000016B362EBA05C-53883D9D', 3, 'Pomarañczowy likier Curacao (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192821-000016B3E80065BC-6417D28E', '20081130192819-000016B362EBA05C-53883D9D', 4, 'Sok cytrynowy (1 ³y¿eczka barowa)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192821-000016B3E82D26FA-E6306143', '20081130192819-000016B362EBA05C-53883D9D', 5, 'Bia³ko (1 jajko)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192821-000016B3E870A1C8-DF67BB02', '20081130192819-000016B362EBA05C-53883D9D', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192839-000016B81C90AB4F-A3FF3CF9', '20081130192837-000016B7C4F61B92-A5E124D9', 1, 'Wódka (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192839-000016B81CAB67C1-1928AF8C', '20081130192837-000016B7C4F61B92-A5E124D9', 2, 'Sok pomarañczowy (60ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192839-000016B81CC1B9A5-D7524C1E', '20081130192837-000016B7C4F61B92-A5E124D9', 3, 'Likier Galliano (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192839-000016B81E3B908A-FA43090A', '20081130192837-000016B7C4F61B92-A5E124D9', 4, 'Wiœnia koktajlowa (1 sz.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192839-000016B81E5949F7-727553CD', '20081130192837-000016B7C4F61B92-A5E124D9', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192852-000016BB40E1091D-4CA27602', '20081130192851-000016BB02D13C68-1C1BA994', 1, 'Bia³y rum (np. Bacardi) - (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192852-000016BB415AA07F-CECF7C9E', '20081130192851-000016BB02D13C68-1C1BA994', 2, 'Likier kokosowy (1 ³y¿ka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192852-000016BB416E64B7-DBF88763', '20081130192851-000016BB02D13C68-1C1BA994', 3, 'Sok z ananasa (2 ³y¿ki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192852-000016BB41D5D004-7F047E53', '20081130192851-000016BB02D13C68-1C1BA994', 4, 'Sok z grejpfruta (1 ³y¿ka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192852-000016BB423B93E8-32AE8898', '20081130192851-000016BB02D13C68-1C1BA994', 5, 'Gazowana woda mineralna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192852-000016BB424F54DA-12750FA5', '20081130192851-000016BB02D13C68-1C1BA994', 6, 'Owoce do dekoracji')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192852-000016BB425FDCCC-F557264B', '20081130192851-000016BB02D13C68-1C1BA994', 7, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192902-000016BD91C42E19-CB1324CD', '20081130192901-000016BD3905013A-9429F948', 1, 'Bia³y rum Bacardi (60ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192902-000016BD927B0B66-81AC2EE5', '20081130192901-000016BD3905013A-9429F948', 2, 'Likier Maraschino (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192902-000016BD92E52DD7-8F962690', '20081130192901-000016BD3905013A-9429F948', 3, 'Sok cytrynowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192902-000016BD932A2350-19C8556E', '20081130192901-000016BD3905013A-9429F948', 4, 'Sok grejpfrutowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192902-000016BD93633F0E-CB185651', '20081130192901-000016BD3905013A-9429F948', 5, 'T³uczony lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192910-000016BF6E508CEE-21ADACD3', '20081130192909-000016BF074E8E9D-5BF8AB64', 1, 'Wino musuj¹ce')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192910-000016BF6E6C7462-1BDA37C8', '20081130192909-000016BF074E8E9D-5BF8AB64', 2, 'Gordon''s Dry Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192910-000016BF6E829988-D0E48C1F', '20081130192909-000016BF074E8E9D-5BF8AB64', 3, 'Likier Drambuie (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192910-000016BF6E987E4B-A0B29B20', '20081130192909-000016BF074E8E9D-5BF8AB64', 4, 'Œwie¿y sok pomarañczowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192910-000016BF6F2EDAA9-07F4A789', '20081130192909-000016BF074E8E9D-5BF8AB64', 5, 'Angostury Bitters (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192910-000016BF6F85602A-177203DD', '20081130192909-000016BF074E8E9D-5BF8AB64', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192918-000016C11F7D858F-44A0EBC6', '20081130192917-000016C0E39AE8C0-8F56BD9B', 1, 'Koniak (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192918-000016C11F95D2E3-EEFA5E3B', '20081130192917-000016C0E39AE8C0-8F56BD9B', 2, 'Œmietanka (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192918-000016C11FABDEF0-DEF61708', '20081130192917-000016C0E39AE8C0-8F56BD9B', 3, 'Likier kawowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192918-000016C11FF7F61B-3A72F21D', '20081130192917-000016C0E39AE8C0-8F56BD9B', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192926-000016C306573064-24F6DB20', '20081130192925-000016C2BE015A1D-02B22951', 1, 'Gordon''s Dry Gin (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192926-000016C30672D318-78666861', '20081130192925-000016C2BE015A1D-02B22951', 2, 'Wytrawny Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192926-000016C306893F2D-7CF36BA6', '20081130192925-000016C2BE015A1D-02B22951', 3, 'Likier Maraschino (25 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192926-000016C3069F550B-F250CCC4', '20081130192925-000016C2BE015A1D-02B22951', 4, 'Sok cytrynowy (1/2 barowej ³y¿ki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192926-000016C306B50684-D9C1C830', '20081130192925-000016C2BE015A1D-02B22951', 5, 'Sok grejpfrutowy (1/2 barowej ³y¿ki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192926-000016C306D02873-7CD1A2AB', '20081130192925-000016C2BE015A1D-02B22951', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192934-000016C4EC763FCA-1C7774FA', '20081130192933-000016C4B09D40E3-75E724CA', 1, 'Rum ciemny (1 i 1/2 oz.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192934-000016C4ECD2FB47-6AEE63D4', '20081130192933-000016C4B09D40E3-75E724CA', 2, 'Creme de Menthe (likier mietowy zielony) - (1/2 oz.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192934-000016C4ECEC7A2A-47DFE9BD', '20081130192933-000016C4B09D40E3-75E724CA', 3, 'Triple Sec albo Curacao albo Blue Bols (1/2 oz.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192934-000016C4EE2C1996-271D135C', '20081130192933-000016C4B09D40E3-75E724CA', 4, 'Sok z limonki (1/2 oz.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192934-000016C4EE89168D-B46DD5A2', '20081130192933-000016C4B09D40E3-75E724CA', 5, 'Cukier (1 ma³a ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192934-000016C4EEB9EE52-0535E764', '20081130192933-000016C4B09D40E3-75E724CA', 6, 'Limonka (1 plasterek na ozdobienie).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192942-000016C6D88E4F8B-ECD8DBFD', '20081130192941-000016C6982F729E-02E08A7F', 1, 'Wino musuj¹ce pó³wytrawne')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192942-000016C6D8AB425C-01946A70', '20081130192941-000016C6982F729E-02E08A7F', 2, 'Bia³y rum Bacardi (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192942-000016C6D90ECB83-69380814', '20081130192941-000016C6982F729E-02E08A7F', 3, 'pomarañczowe Curacao (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192942-000016C6DA1C2E52-F1281D90', '20081130192941-000016C6982F729E-02E08A7F', 4, 'Likier miêtowy zielony (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192942-000016C6DA4E4D79-7AC19493', '20081130192941-000016C6982F729E-02E08A7F', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192950-000016C8BA90256A-5FAED60D', '20081130192949-000016C87E47FC5F-E9D48E46', 1, 'Tequila (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192950-000016C8BAED8380-9EB8AE0E', '20081130192949-000016C87E47FC5F-E9D48E46', 2, 'Barack Liqueur (likier morelowy) - (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192950-000016C8BB16846D-8503ECAD', '20081130192949-000016C87E47FC5F-E9D48E46', 3, 'Sok z pomarañczy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192950-000016C8BBEA5373-9A2E3085', '20081130192949-000016C87E47FC5F-E9D48E46', 4, 'Sok z maracuji (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192950-000016C8BC1CBBB7-B7038413', '20081130192949-000016C87E47FC5F-E9D48E46', 5, 'Sok z ananasa (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192950-000016C8BCBAA5D0-E16918E5', '20081130192949-000016C87E47FC5F-E9D48E46', 6, 'Sok z cytryny (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192950-000016C8BCD3578A-B8CCB2CC', '20081130192949-000016C87E47FC5F-E9D48E46', 7, 'Pomarañcz (1/2 plasterka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192950-000016C8BCE9304C-E14514DD', '20081130192949-000016C87E47FC5F-E9D48E46', 8, '1 zielona wiœnia koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192950-000016C8BDE36D1E-ABAFDCB5', '20081130192949-000016C87E47FC5F-E9D48E46', 9, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192958-000016CA87C8A698-C68A764D', '20081130192957-000016CA3D550615-C2AFD8DE', 1, 'Wódka Bols (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192958-000016CA888F2594-6DC3B271', '20081130192957-000016CA3D550615-C2AFD8DE', 2, 'Likier Bols Blue (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192958-000016CA88C4495A-8A7B12E9', '20081130192957-000016CA3D550615-C2AFD8DE', 3, 'Sok ananasowy (120ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192958-000016CA88DE704B-B9A22158', '20081130192957-000016CA3D550615-C2AFD8DE', 4, 'Sok bananowy (120ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130192958-000016CA88F5606A-6CBBFD35', '20081130192957-000016CA3D550615-C2AFD8DE', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193008-000016CCCAE99E7B-C95C1457', '20081130193007-000016CC8B4829F3-5D409FB0', 1, 'Wódka (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193008-000016CCCB53F207-2DE65140', '20081130193007-000016CC8B4829F3-5D409FB0', 2, 'Likier Archers (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193008-000016CCCBE024CE-15EB47FA', '20081130193007-000016CC8B4829F3-5D409FB0', 3, 'Grenadine (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193008-000016CCCBFAC5F8-9616A0F6', '20081130193007-000016CC8B4829F3-5D409FB0', 4, 'Sok ananasowy (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193008-000016CCCC111A0A-E8BA348E', '20081130193007-000016CC8B4829F3-5D409FB0', 5, 'Sok z cytryny (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193008-000016CCCC26CB83-9A0FAE2E', '20081130193007-000016CC8B4829F3-5D409FB0', 6, 'Lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193017-000016CF06E42FC4-72DF05C9', '20081130193016-000016CECF3963D5-A2BE2C1A', 1, 'Wytrawne wino musuj¹ce lub szampan (100 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193017-000016CF07D0FAB9-C0B42CB4', '20081130193016-000016CECF3963D5-A2BE2C1A', 2, 'Creme de Cassis (10 ml).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193026-000016D1008BDE28-C3A47512', '20081130193025-000016D0C9123ABF-6BC26E32', 1, 'Ciemny rum Jamaica (75%) - (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193026-000016D100DE6CC8-34237792', '20081130193025-000016D0C9123ABF-6BC26E32', 2, 'Likier pomarañczowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193026-000016D101BDDDE2-FE15BF69', '20081130193025-000016D0C9123ABF-6BC26E32', 3, 'Czerwony wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193026-000016D101D70002-1EE9808C', '20081130193025-000016D0C9123ABF-6BC26E32', 4, 'Syrop grenadynowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193026-000016D102F1F9C9-3075810E', '20081130193025-000016D0C9123ABF-6BC26E32', 5, 'Sok z 1 cytryny (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193026-000016D103533A05-F25C478C', '20081130193025-000016D0C9123ABF-6BC26E32', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193035-000016D333BBDEBB-ED230EA8', '20081130193034-000016D302F9FCEA-9F33D87B', 1, 'Brzoskwiniowa wódka (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193035-000016D333FCA494-9BB7BF99', '20081130193034-000016D302F9FCEA-9F33D87B', 2, 'Likier Baileys (kilka kropel)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193035-000016D334335592-2B3519DA', '20081130193034-000016D302F9FCEA-9F33D87B', 3, 'Grenadine (kilka kropel)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193043-000016D4E162B92A-75319728', '20081130193042-000016D4AA7BCE2D-84251567', 1, 'Brandy (1 1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193043-000016D4E1BFCBF4-B3488572', '20081130193042-000016D4AA7BCE2D-84251567', 2, 'Creme de Banana (1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193043-000016D4E21421A4-6081D2DB', '20081130193042-000016D4AA7BCE2D-84251567', 3, 'Sok pomarañczowy (1 tsp)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193043-000016D4E2638C00-414915C0', '20081130193042-000016D4AA7BCE2D-84251567', 4, 'Sok cytrynowy (2 tsp)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193043-000016D4E2D7010C-4A499560', '20081130193042-000016D4AA7BCE2D-84251567', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193051-000016D6C26B3878-478E9114', '20081130193049-000016D6776A93A9-DAD79AC4', 1, 'Brandy (1 1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193051-000016D6C2B589AC-0313DF2D', '20081130193049-000016D6776A93A9-DAD79AC4', 2, 'Bia³y Creme de Menthe (1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193051-000016D6C2CF30EF-D1241EA5', '20081130193049-000016D6776A93A9-DAD79AC4', 3, 'S³odki Wermut (1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193051-000016D6C2E71E3C-11B9A2C9', '20081130193049-000016D6776A93A9-DAD79AC4', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193059-000016D8D5E53D68-70362DC6', '20081130193059-000016D89AC2F59C-E3337A9F', 1, 'Gordon''s Dry Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193059-000016D8D6056A50-64310342', '20081130193059-000016D89AC2F59C-E3337A9F', 2, 'Creme de Cassis Boudier (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193100-000016D8D6B0E703-3EEEC3C0', '20081130193059-000016D89AC2F59C-E3337A9F', 3, 'Amaretto di Saronno (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193100-000016D8D6CD8A2A-299518B0', '20081130193059-000016D89AC2F59C-E3337A9F', 4, 'Wytrawne wino musuj¹ce')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193100-000016D8D6EB7910-5666D21C', '20081130193059-000016D89AC2F59C-E3337A9F', 5, 'Skórka z pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193100-000016D8D722B5BD-061F32AA', '20081130193059-000016D89AC2F59C-E3337A9F', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193108-000016DAEAA19372-66C97EC6', '20081130193107-000016DA958B429D-26CE6A6C', 1, 'Canadian Club Whisky (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193108-000016DAEABD02DC-A3B22A18', '20081130193107-000016DA958B429D-26CE6A6C', 2, 'Bia³y rum Bacardi (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193108-000016DAEB1A9CF7-B7317FD2', '20081130193107-000016DA958B429D-26CE6A6C', 3, 'Likier any¿owy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193108-000016DAEB9D700B-7FE348EA', '20081130193107-000016DA958B429D-26CE6A6C', 4, 'Wódka any¿owa Pastis (25 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193108-000016DAEBB69571-D8E674FA', '20081130193107-000016DA958B429D-26CE6A6C', 5, 'Angostury Bitters (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193108-000016DAEBFD9A00-89F0D139', '20081130193107-000016DA958B429D-26CE6A6C', 6, 'Orange Bitters (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193108-000016DAEC3871E4-22AA766B', '20081130193107-000016DA958B429D-26CE6A6C', 7, 'Syrop grenadynowy (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193108-000016DAEC9BC7C2-3F02A5F9', '20081130193107-000016DA958B429D-26CE6A6C', 8, 'Skórka cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193108-000016DAED035829-222E9D04', '20081130193107-000016DA958B429D-26CE6A6C', 9, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193117-000016DCD3BE7FAF-D167CE5E', '20081130193115-000016DC80FF793A-746AAFF0', 1, 'Tequila Sierra srebna (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193117-000016DCD40A5E1B-977D0150', '20081130193115-000016DC80FF793A-746AAFF0', 2, 'Likier brzoskwiniowy (25ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193117-000016DCD481D950-D6A7A680', '20081130193115-000016DC80FF793A-746AAFF0', 3, 'Grenadina (15ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193117-000016DCD49F5F73-E94F637E', '20081130193115-000016DC80FF793A-746AAFF0', 4, 'Sok ananasowy.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193124-000016DE9F1EEC2B-C8B95CF9', '20081130193123-000016DE62FB07A7-403D1D94', 1, 'Wódka Finlandia Grejfrutowa (50cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193124-000016DE9F61C119-4EC2A971', '20081130193123-000016DE62FB07A7-403D1D94', 2, 'Likier Amaretto (20cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193124-000016DE9FC5FC21-EBCD072E', '20081130193123-000016DE62FB07A7-403D1D94', 3, 'Syrop truskawkowy (10cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193124-000016DEA00F070B-BB4ADC41', '20081130193123-000016DE62FB07A7-403D1D94', 4, 'Sok grejfrutowy (60cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193124-000016DEA08AB984-7CADFADE', '20081130193123-000016DE62FB07A7-403D1D94', 5, 'top-tonic.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193133-000016E0B4C32406-D4FC1333', '20081130193132-000016E06E311FA2-B93A6AF9', 1, 'Gin (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193133-000016E0B54EFA0B-7754B9D1', '20081130193132-000016E06E311FA2-B93A6AF9', 2, 'Ciemny rum (1/2 miarki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193133-000016E0B5663AEC-B4146794', '20081130193132-000016E06E311FA2-B93A6AF9', 3, 'Likier Cointreau (1/2 miarki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193133-000016E0B5B9FE60-14CB75FA', '20081130193132-000016E06E311FA2-B93A6AF9', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193207-000016E8779BFDE9-79E4003C', '20081130193205-000016E82FF4125C-48BF1E37', 1, 'Gordon''s Dry Gin (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193207-000016E877BB5FD7-CE405412', '20081130193205-000016E82FF4125C-48BF1E37', 2, 'Likier Amaretto di Saronno (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193207-000016E877D39641-2F629B37', '20081130193205-000016E82FF4125C-48BF1E37', 3, 'Sok cytrynowy (5ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193207-000016E877E99F07-B234320D', '20081130193205-000016E82FF4125C-48BF1E37', 4, 'Sok morelowy (80ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193207-000016E8784FB5DA-EF721DE9', '20081130193205-000016E82FF4125C-48BF1E37', 5, 'Wiœnie koktajlowe (2 szt.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193207-000016E878BB75E2-55513EE3', '20081130193205-000016E82FF4125C-48BF1E37', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193223-000016EC39D0E678-33E3C17C', '20081130193221-000016EBEBF93FBF-C616EB62', 1, 'Likier Southern Comfort (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193223-000016EC39EC6A9E-0902B982', '20081130193221-000016EBEBF93FBF-C616EB62', 2, 'Sok cytrynowy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193223-000016EC3A027B08-7351E740', '20081130193221-000016EBEBF93FBF-C616EB62', 3, 'Syrop Grenadynowy (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193223-000016EC3A73820E-2293B903', '20081130193221-000016EBEBF93FBF-C616EB62', 4, 'Woda mineralna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193223-000016EC3AB97E86-3D21B966', '20081130193221-000016EBEBF93FBF-C616EB62', 5, 'Plaster pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193223-000016EC3BAD795D-4DA7ECB4', '20081130193221-000016EBEBF93FBF-C616EB62', 6, 'Wiœnie koktajlowe (2szt.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193223-000016EC3BCB4ACC-326FA25C', '20081130193221-000016EBEBF93FBF-C616EB62', 7, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193231-000016EE1DB85F45-AB8295F9', '20081130193230-000016EDE42531A0-577667FD', 1, 'Likier miêtowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193231-000016EE1E22BCA4-AB1CBA61', '20081130193230-000016EDE42531A0-577667FD', 2, 'Wytrawne bia³e Martini (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193231-000016EE1E3AF425-A538A66B', '20081130193230-000016EDE42531A0-577667FD', 3, 'Napój miêtowo-jab³kowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193231-000016EE1E519D56-E1F2BCCD', '20081130193230-000016EDE42531A0-577667FD', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193239-000016F01C98BC48-58D29CD8', '20081130193238-000016EFE17DBFA5-F3DBA9B2', 1, 'Gin (np. Gordon''s Dry Gin) (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193239-000016F01CD1EBAB-3764B816', '20081130193238-000016EFE17DBFA5-F3DBA9B2', 2, 'Creme de Menthe (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193239-000016F01D504C8C-80AD0722', '20081130193238-000016EFE17DBFA5-F3DBA9B2', 3, 'Szampan')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193239-000016F01D6A8EC5-329D26DE', '20081130193238-000016EFE17DBFA5-F3DBA9B2', 4, 'Sok z 1/2 cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193239-000016F01D8075B7-C7A0793B', '20081130193238-000016EFE17DBFA5-F3DBA9B2', 5, 'Cukier (1 ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193239-000016F01E06B3FD-F9EABAAD', '20081130193238-000016EFE17DBFA5-F3DBA9B2', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193248-000016F21EF4B8C6-E552CAD5', '20081130193247-000016F1E5759BBD-960EBA91', 1, 'Wódka Finlandia (2 cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193248-000016F21F69092B-75D27AE5', '20081130193247-000016F1E5759BBD-960EBA91', 2, 'Likier Créme De Cassis (2 cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193248-000016F21F85AD69-C6A2056A', '20081130193247-000016F1E5759BBD-960EBA91', 3, 'Sok grejpfrutowy (8 cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193248-000016F21F9CFA4B-1155E52E', '20081130193247-000016F1E5759BBD-960EBA91', 4, 'kruszony lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193256-000016F3F2CE8D49-CD023D52', '20081130193255-000016F39E2DA54E-8AFC0997', 1, 'Likier migda³owy Niebieskie Migda³y (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193256-000016F3F2EAD3AE-D472FE3F', '20081130193255-000016F39E2DA54E-8AFC0997', 2, 'Mleko skondensowane nies³odzone lub œmietanka (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193256-000016F3F300C58A-1792C993', '20081130193255-000016F39E2DA54E-8AFC0997', 3, 'Sok ananasowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193256-000016F3F31668D3-42BC162D', '20081130193255-000016F39E2DA54E-8AFC0997', 4, 'pokruszony lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193256-000016F3F32BFBBE-ABE81D68', '20081130193255-000016F39E2DA54E-8AFC0997', 5, 'wisienka koktajlowa.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193307-000016F68A001F7C-BDC3B76D', '20081130193306-000016F648AB0559-AEF25C27', 1, 'Wino musuj¹ce (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193307-000016F68A1BFD1E-6D66E5D4', '20081130193306-000016F648AB0559-AEF25C27', 2, 'Whisky (np. Canadian Club Whisky) - (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193307-000016F68A32741C-B4FEC1C4', '20081130193306-000016F648AB0559-AEF25C27', 3, 'Angostury Bitter (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193307-000016F68A48859D-6DAF95DC', '20081130193306-000016F648AB0559-AEF25C27', 4, 'Bia³e Curacao (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193307-000016F68AD5223F-CFFA5D05', '20081130193306-000016F648AB0559-AEF25C27', 5, 'Likier Maraschino (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193307-000016F68AEFB422-28759A4F', '20081130193306-000016F648AB0559-AEF25C27', 6, 'Pomarañcz (plasterek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193307-000016F68B0632C3-F5D256DC', '20081130193306-000016F648AB0559-AEF25C27', 7, 'Wiœnia koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193307-000016F68B1BD60D-93704265', '20081130193306-000016F648AB0559-AEF25C27', 8, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193317-000016F8BF5EBE9F-3BF2E13B', '20081130193315-000016F8746E7CA7-59B7F4BF', 1, 'Bia³y rum Bacardi (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193317-000016F8BFC9D9DF-8D1FCA44', '20081130193315-000016F8746E7CA7-59B7F4BF', 2, 'Bia³e Curacao (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193317-000016F8C03D5AEC-D330D45B', '20081130193315-000016F8746E7CA7-59B7F4BF', 3, 'Bia³y Creme de Cacao (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193317-000016F8C05600A5-E9839EAC', '20081130193315-000016F8746E7CA7-59B7F4BF', 4, 'Wódka any¿owa Pastis (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193317-000016F8C06C722F-F4BB1D7C', '20081130193315-000016F8746E7CA7-59B7F4BF', 5, '1 ¿ó³tko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193317-000016F8C0825D7E-FAEDD4AA', '20081130193315-000016F8746E7CA7-59B7F4BF', 6, 'Sok z 1 cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193317-000016F8C09BE862-AFE19B31', '20081130193315-000016F8746E7CA7-59B7F4BF', 7, 'Cukier puder (1 ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193317-000016F8C123EECF-9EB17382', '20081130193315-000016F8746E7CA7-59B7F4BF', 8, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193325-000016FACC316070-F4C4DB65', '20081130193325-000016FA98E98B36-162BAA6A', 1, 'Cointreau (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193325-000016FACC66531A-008296D5', '20081130193325-000016FA98E98B36-162BAA6A', 2, 'Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193325-000016FACC864BA1-59297EFC', '20081130193325-000016FA98E98B36-162BAA6A', 3, 'Sok pomarañczowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193325-000016FACC9E1C8E-F6DDB5C3', '20081130193325-000016FA98E98B36-162BAA6A', 4, 'Œmietana (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193325-000016FACCB49D5E-6DBD3D6D', '20081130193325-000016FA98E98B36-162BAA6A', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193419-000017073D0035DE-153202B5', '20081130193418-00001706F4EEB048-64D78FFF', 1, 'Wódka Finlandia (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193419-000017073D1BD31D-71AB24F6', '20081130193418-00001706F4EEB048-64D78FFF', 2, 'Amaretto di Saronno (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193419-000017073D84F74A-CCC675FB', '20081130193418-00001706F4EEB048-64D78FFF', 3, 'Sok pomarañczowy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193419-000017073DA1CED2-22263B39', '20081130193418-00001706F4EEB048-64D78FFF', 4, 'Syrop grenadynowy (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193419-000017073DBA4141-D8AFDE24', '20081130193418-00001706F4EEB048-64D78FFF', 5, '£ód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193435-0000170AFEB131CD-6E362C69', '20081130193434-0000170AB651F9A4-489CEFC9', 1, 'Gordon''s Dry Gin (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193435-0000170AFECCD253-BA367391', '20081130193434-0000170AB651F9A4-489CEFC9', 2, 'Likier Creme de Cassis Boudier (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193435-0000170AFEE47F3D-3B9353B0', '20081130193434-0000170AB651F9A4-489CEFC9', 3, 'Sok cytrynowy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193435-0000170AFEFB99EC-C8F5A527', '20081130193434-0000170AB651F9A4-489CEFC9', 4, 'Woda sodowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193435-0000170AFF12D9B5-6E333990', '20081130193434-0000170AB651F9A4-489CEFC9', 5, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193435-0000170AFF29C68E-E91E66F1', '20081130193434-0000170AB651F9A4-489CEFC9', 6, 'Limonka (1 plaster).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193450-0000170E7EDB6C7D-3E426BAD', '20081130193447-0000170DCECA7C96-F291F977', 1, 'Wino musuj¹ce')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193450-0000170E7EF6B49D-B47AC6AC', '20081130193447-0000170DCECA7C96-F291F977', 2, 'Likier pomarañczowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193450-0000170E7F0E04C5-04631CE2', '20081130193447-0000170DCECA7C96-F291F977', 3, 'Sok pomarañczowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193450-0000170E7F42DB10-1F174B6B', '20081130193447-0000170DCECA7C96-F291F977', 4, 'Syrop pomarañczowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193450-0000170E7F62655E-D87B4ADD', '20081130193447-0000170DCECA7C96-F291F977', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193520-0000171599011736-E26AA4DD', '20081130193519-00001715501316F7-BF5B1CC9', 1, 'Wytrawne wino musuj¹ce')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193520-000017159949426A-DB7CA823', '20081130193519-00001715501316F7-BF5B1CC9', 2, 'Likier Parfait Amour (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193520-0000171599664024-0A612691', '20081130193519-00001715501316F7-BF5B1CC9', 3, '10 malin')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193521-00001715AA35CB07-D4803689', '20081130193519-00001715501316F7-BF5B1CC9', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193529-0000171795521403-63068586', '20081130193528-0000171758FCA292-23CB1C34', 1, 'Wódka Smirnoff (1 oz.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193529-00001717956E2606-F03C11A9', '20081130193528-0000171758FCA292-23CB1C34', 2, 'Likier kawowy (1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193529-0000171795858117-6ABC0A2E', '20081130193528-0000171758FCA292-23CB1C34', 3, 'Likier Amaretto (1 tspn)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193529-0000171795D8A996-4F9CEF32', '20081130193528-0000171758FCA292-23CB1C34', 4, 'Wisienka koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193529-0000171795FDF633-4597DA8D', '20081130193528-0000171758FCA292-23CB1C34', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193549-0000171C360EF89B-39161498', '20081130193548-0000171BFEDD01CF-902D8617', 1, 'Bia³y rum Bacardi (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193549-0000171C362B6302-96EE2660', '20081130193548-0000171BFEDD01CF-902D8617', 2, 'Brandy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193549-0000171C368C57F2-9800DFAB', '20081130193548-0000171BFEDD01CF-902D8617', 3, 'Likier Amaretto di Saronno (15ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193549-0000171C36A65454-20D1C34F', '20081130193548-0000171BFEDD01CF-902D8617', 4, 'Sok cytrynowy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193549-0000171C36BD8A4B-DD7D2DE4', '20081130193548-0000171BFEDD01CF-902D8617', 5, 'Œwie¿y sok pomarañczowy (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193549-0000171C36D4E78B-0CE72E81', '20081130193548-0000171BFEDD01CF-902D8617', 6, 'Melisa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193549-0000171C36EC023A-EB5F15C4', '20081130193548-0000171BFEDD01CF-902D8617', 7, 'T³uczony lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193607-000017205E5798C3-5CCC4A0B', '20081130193606-000017201E769108-D2477CDC', 1, 'Irish Cream Baileys (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193607-000017205E73694D-F399B6CB', '20081130193606-000017201E769108-D2477CDC', 2, 'Likier Kahlua (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193607-000017205E8A9F44-CD4883BC', '20081130193606-000017201E769108-D2477CDC', 3, 'Wódka (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193607-000017205EA195F0-14F8180C', '20081130193606-000017201E769108-D2477CDC', 4, 'Likier Amaretto (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193607-000017205F00A99F-16B4EB2A', '20081130193606-000017201E769108-D2477CDC', 5, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193607-000017205F237A06-F78B7389', '20081130193606-000017201E769108-D2477CDC', 6, 'wisienki koktajlowe.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193617-00001722A7705C2D-801DE125', '20081130193616-00001722756A7291-42720A42', 1, 'Creme de cassis (2cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193617-00001722A78DF5F4-02946F9A', '20081130193616-00001722756A7291-42720A42', 2, 'Sour apple (2cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193617-00001722A7E52384-2AAE6F31', '20081130193616-00001722756A7291-42720A42', 3, 'Southern Comfort (2cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193617-00001722A800DE3A-4D8C0398', '20081130193616-00001722756A7291-42720A42', 4, 'Wódka Finlandia Lime (3cl)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193617-00001722A8197395-85C1C0AB', '20081130193616-00001722756A7291-42720A42', 5, 'Sprite')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193617-00001722A83096FF-413E4C80', '20081130193616-00001722756A7291-42720A42', 6, 'Kruszony lód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193617-00001722A847CBDE-E8ADA3F2', '20081130193616-00001722756A7291-42720A42', 7, 'Truskawki')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193617-00001722A85F4695-93E680C8', '20081130193616-00001722756A7291-42720A42', 8, 'Borówki.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193625-000017248D3E11C7-794C39DD', '20081130193624-0000172460AC9C93-9BA13F4A', 1, 'Creme de Cassis Boudier (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193625-000017248D5B220E-A74C7E85', '20081130193624-0000172460AC9C93-9BA13F4A', 2, 'Likier pomarañczowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193625-000017248D731072-90C04112', '20081130193624-0000172460AC9C93-9BA13F4A', 3, 'Ró¿owy Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193625-000017248D8BAB42-44AB6DD4', '20081130193624-0000172460AC9C93-9BA13F4A', 4, 'Tonik')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193625-000017248DA3725C-28A63E7D', '20081130193624-0000172460AC9C93-9BA13F4A', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193634-00001726A80023CC-8B647358', '20081130193633-000017266780CFC2-A4928904', 1, 'Remy Martin TM Cognac (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193634-00001726A8511EA7-C5F86558', '20081130193633-000017266780CFC2-A4928904', 2, 'Likier Cointreau (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193634-00001726A86C08EE-A3D716B5', '20081130193633-000017266780CFC2-A4928904', 3, 'Sok cytrynowy (5 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193634-00001726A88355D0-93CCCE97', '20081130193633-000017266780CFC2-A4928904', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193643-00001728B6D7B904-B7108844', '20081130193641-0000172873D8A55C-18B797E8', 1, 'Wódka Absolut Kurant (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193643-00001728B75474AA-3F52C1D2', '20081130193641-0000172873D8A55C-18B797E8', 2, 'Likier Curacao (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193643-00001728B7705D36-57BFDBD4', '20081130193641-0000172873D8A55C-18B797E8', 3, 'Syrop brzoskwiniowy (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193643-00001728B789F878-1438A144', '20081130193641-0000172873D8A55C-18B797E8', 4, 'Syrop malinowy (kilka kropel)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193643-00001728B7A81305-07D0FC1E', '20081130193641-0000172873D8A55C-18B797E8', 5, 'Fanta')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193643-00001728B7C15928-7016B44D', '20081130193641-0000172873D8A55C-18B797E8', 6, 'Lód (6 kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193651-0000172AB1D6EEAC-C5F32647', '20081130193650-0000172A80A3F68E-7C3D1E64', 1, 'Wódka Finlandia (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193651-0000172AB21EB462-C27D4692', '20081130193650-0000172A80A3F68E-7C3D1E64', 2, 'Amaretto di Saronno (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193651-0000172AB238C9DD-391E215D', '20081130193650-0000172A80A3F68E-7C3D1E64', 3, 'S³odka œmietanka (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193651-0000172AB25030F0-B4793F24', '20081130193650-0000172A80A3F68E-7C3D1E64', 4, 'Ga³ka muszkato³owa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193651-0000172AB2669561-6F63D2D8', '20081130193650-0000172A80A3F68E-7C3D1E64', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193658-0000172C59978940-6F8573F2', '20081130193657-0000172C224A530E-155EC844', 1, 'Gordon''s Dry Gin (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193658-0000172C59CF1B0B-6142F82B', '20081130193657-0000172C224A530E-155EC844', 2, 'Likier morelowy Barack Liqueur (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193658-0000172C59E8E30B-8ACACD97', '20081130193657-0000172C224A530E-155EC844', 3, 'Sok cytrynowy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193658-0000172C59FFD214-ED66B895', '20081130193657-0000172C224A530E-155EC844', 4, 'Pomarañcz (1 plaster)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193658-0000172C5A165FFD-029A6FAC', '20081130193657-0000172C224A530E-155EC844', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193706-0000172E221D96CF-A98BE8AE', '20081130193705-0000172DDA0B04C6-6FD48BC3', 1, 'Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193706-0000172E223A7086-8839C14D', '20081130193705-0000172DDA0B04C6-6FD48BC3', 2, 'Likier Cointreau (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193706-0000172E225185C0-475623ED', '20081130193705-0000172DDA0B04C6-6FD48BC3', 3, 'Sok brzoskwiniowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193706-0000172E22682BAB-64D1203D', '20081130193705-0000172DDA0B04C6-6FD48BC3', 4, 'Woda sodowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193706-0000172E227E947A-0AA004DC', '20081130193705-0000172DDA0B04C6-6FD48BC3', 5, 'Pomarañcz (1 plaster)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193706-0000172E22951BD6-C1C34731', '20081130193705-0000172DDA0B04C6-6FD48BC3', 6, 'Miêta (listek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193706-0000172E22AB5A16-F90AF77F', '20081130193705-0000172DDA0B04C6-6FD48BC3', 7, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193714-0000173016964C7F-FD1A6A4D', '20081130193713-0000172FE7585557-8228FA35', 1, 'Likier Southern Comfort (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193714-0000173016AF04C5-CF3FCFA7', '20081130193713-0000172FE7585557-8228FA35', 2, 'Syrop grenadynowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193714-0000173016C618E8-6B66023D', '20081130193713-0000172FE7585557-8228FA35', 3, 'Sok cytrynowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193714-0000173016DD0A1F-5619C49C', '20081130193713-0000172FE7585557-8228FA35', 4, 'Œwie¿y sok pomarañczowy (60 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193714-0000173016F3D981-DA488904', '20081130193713-0000172FE7585557-8228FA35', 5, 'Pomarañcz (1 plaster)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193714-00001730172D794B-4E1D8C34', '20081130193713-0000172FE7585557-8228FA35', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193722-00001731DD305238-76959CDE', '20081130193721-00001731A3F55A39-6DA8F43F', 1, 'Likier Southern Comfort (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193722-00001731DD4D2AD8-1E716D0A', '20081130193721-00001731A3F55A39-6DA8F43F', 2, 'Bia³y rum Bacardi (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193722-00001731DD640EF6-8616E31D', '20081130193721-00001731A3F55A39-6DA8F43F', 3, 'Sok grejpfrutowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193722-00001731DD7B637C-FE76CA7A', '20081130193721-00001731A3F55A39-6DA8F43F', 4, 'Syrop grenadynowy (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193722-00001731DD9E4CFC-BB066182', '20081130193721-00001731A3F55A39-6DA8F43F', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193731-00001733E9633E83-96A6B774', '20081130193729-0000173395977A08-0C0D4A23', 1, 'Brandy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193731-00001733E97FEDAB-F921A6AF', '20081130193729-0000173395977A08-0C0D4A23', 2, 'Bia³y likier Creme de Menthe (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193731-00001733E996E56E-B585779B', '20081130193729-0000173395977A08-0C0D4A23', 3, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193742-000017367D4999B0-340332CA', '20081130193741-000017363C996A9A-CFF395AB', 1, 'Gordon''s Dry Gin (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193742-000017367D65557D-84084CEC', '20081130193741-000017363C996A9A-CFF395AB', 2, 'Bia³y rum Bacardi (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193742-000017367D7CCF1D-9FC0A013', '20081130193741-000017363C996A9A-CFF395AB', 3, 'Likier morelowy Barack Liqueur (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193742-000017367D93C283-033BC5DC', '20081130193741-000017363C996A9A-CFF395AB', 4, 'Sok z maracuji (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193742-000017367DAA55E1-F0191D9F', '20081130193741-000017363C996A9A-CFF395AB', 5, 'Sok ze œwie¿ej pomarañczy (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193742-000017367DC0ED9C-D9D5F04A', '20081130193741-000017363C996A9A-CFF395AB', 6, 'Syrop grenadynowy (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193742-000017367DD76C3D-D9262375', '20081130193741-000017363C996A9A-CFF395AB', 7, 'Pomarañcz (1 plaster)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193742-000017367DEE096D-B2F78108', '20081130193741-000017363C996A9A-CFF395AB', 8, 'Wiœnia koktajlowa (1szt.)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193742-000017367E0460C6-6CC52B92', '20081130193741-000017363C996A9A-CFF395AB', 9, 'Melisa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193742-000017367E1B4942-023A3A0A', '20081130193741-000017363C996A9A-CFF395AB', 10, 'Lód (kilka kost')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193753-0000173925AA47F4-A54E11EE', '20081130193752-00001738C6D0D0FF-A75733E8', 1, 'Wódka czysta (1 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193753-0000173925C7054B-AD139478', '20081130193752-00001738C6D0D0FF-A75733E8', 2, 'Likier Amaretto (1/2 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193753-0000173925DDEB99-30F534B3', '20081130193752-00001738C6D0D0FF-A75733E8', 3, 'Œmietana (3/8 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193753-0000173925F4BC13-7365F936', '20081130193752-00001738C6D0D0FF-A75733E8', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193802-0000173B1960DE4B-3DF7442C', '20081130193800-0000173AD4ECCEB2-D4B593F7', 1, 'Rum Havana Club (25ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193802-0000173B197C6B2C-AF2A6D07', '20081130193800-0000173AD4ECCEB2-D4B593F7', 2, 'Likier Bols Melon (25ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193802-0000173B19935806-C11F4A60', '20081130193800-0000173AD4ECCEB2-D4B593F7', 3, 'Sok z limonki (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193802-0000173B19AA1195-861C5FB5', '20081130193800-0000173AD4ECCEB2-D4B593F7', 4, 'Syrop cukrowy (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193802-0000173B19C2C57E-D44B640B', '20081130193800-0000173AD4ECCEB2-D4B593F7', 5, 'Sok ananasowy (100ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193802-0000173B19D90EA7-D8BED106', '20081130193800-0000173AD4ECCEB2-D4B593F7', 6, 'Lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193809-0000173CDF6BAA70-02579C92', '20081130193808-0000173CA654D0C6-D67ABA0F', 1, 'Ciemny Rum (1/2 miarki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193809-0000173CDF887826-C7E2F8CD', '20081130193808-0000173CA654D0C6-D67ABA0F', 2, 'Sok z cytryny (1/4 miarki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193809-0000173CDF9FC2D9-D076F7AD', '20081130193808-0000173CA654D0C6-D67ABA0F', 3, 'Cointreau (likier o delikatnym aromacie pomarañczowym) - (1/4 miarki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193809-0000173CDFC61217-2F73E834', '20081130193808-0000173CA654D0C6-D67ABA0F', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193854-0000174732FCF298-DA6A9406', '20081130193853-00001746F7D6FE1F-ACEF1941', 1, 'Rum Jamaica (30ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193854-0000174733CE57F5-90AF82BB', '20081130193853-00001746F7D6FE1F-ACEF1941', 2, 'Likier bananowy (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193854-0000174734DA7E4B-BA29AC9E', '20081130193853-00001746F7D6FE1F-ACEF1941', 3, 'Sok pomarañczowy (10ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193854-00001747390BCDE3-D34ACD2E', '20081130193853-00001746F7D6FE1F-ACEF1941', 4, 'Sok cytrynowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193854-000017473ACC5E0C-069154D0', '20081130193853-00001746F7D6FE1F-ACEF1941', 5, 'Cytryna (1 plaster)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193854-000017473B220092-2C3CBA67', '20081130193853-00001746F7D6FE1F-ACEF1941', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193903-000017494E545B13-5A40A1BD', '20081130193902-000017491B7EB03D-21759581', 1, 'Campari (1.5 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193903-000017494E6914D4-F108681A', '20081130193902-000017491B7EB03D-21759581', 2, 'Likier Cointreau (1 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193903-000017494E7A50D5-CDDF94F2', '20081130193902-000017491B7EB03D-21759581', 3, 'Sok cytrynowy (1 oz)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193903-000017494E8B6C19-D6FAD96F', '20081130193902-000017491B7EB03D-21759581', 4, 'Tonik do uzupe³nienia')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193903-000017494E9C7816-C6E5930A', '20081130193902-000017491B7EB03D-21759581', 5, 'Pomarañcz (plasterek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193903-000017494EAD8BB7-6BD17976', '20081130193902-000017491B7EB03D-21759581', 6, 'Skórka pomarañczowa.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193931-0000175007DABF55-5731F08D', '20081130193931-0000174FDD2408DD-3FC74204', 1, 'Jameson Irish Whisky (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193931-0000175007F65DAB-C449A8E7', '20081130193931-0000174FDD2408DD-3FC74204', 2, 'Likier zielony Chartreuse (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193931-00001750082C5526-D8321E1C', '20081130193931-0000174FDD2408DD-3FC74204', 3, 'Czerwony Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193931-000017500859AE14-699D59D5', '20081130193931-0000174FDD2408DD-3FC74204', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193939-00001751BAB36DA0-251ED22A', '20081130193938-000017518C2B4869-E84D0ED0', 1, 'Whisky (1 miarka )')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193939-00001751BACF4C59-AAFF7816', '20081130193938-000017518C2B4869-E84D0ED0', 2, 'Gin (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193939-00001751BAE61647-D7CF9B9E', '20081130193938-000017518C2B4869-E84D0ED0', 3, 'Likier miêtowy (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193939-00001751BB07B0C2-BA4E0388', '20081130193938-000017518C2B4869-E84D0ED0', 4, 'Sok z limonki')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193939-00001751BB4AACFA-91AC3FBC', '20081130193938-000017518C2B4869-E84D0ED0', 5, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193939-00001751BB65433A-8E40891C', '20081130193938-000017518C2B4869-E84D0ED0', 6, 'Listek miêty.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193951-000017549379FCD3-CC96F5B4', '20081130193950-000017545842B457-A2D75726', 1, 'Likier bananowy Pisang Ambon (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193951-0000175493965A23-5E06CAC7', '20081130193950-000017545842B457-A2D75726', 2, 'Bia³y rum Bacardi (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193951-0000175493AE2C27-1DAB01B0', '20081130193950-000017545842B457-A2D75726', 3, 'Œwie¿y sok pomarañczowy (50ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193951-0000175493C68465-669C630C', '20081130193950-000017545842B457-A2D75726', 4, 'S³odka œmietanka (20ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193951-0000175493DE8E11-6C790F92', '20081130193950-000017545842B457-A2D75726', 5, 'Miêta (1 ga³¹zka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130193951-0000175493F57230-A68B6D7B', '20081130193950-000017545842B457-A2D75726', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194000-00001756C207FACE-29A1D858', '20081130193959-0000175687F53545-F29645CE', 1, 'Canadian Club Whisky (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194000-00001756C252F2F9-550D7E80', '20081130193959-0000175687F53545-F29645CE', 2, 'Wytrawne Sherry (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194000-00001756C26B1161-8C3CA1A1', '20081130193959-0000175687F53545-F29645CE', 3, 'Likier pomarañczowy (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194000-00001756C281C235-5A59AB94', '20081130193959-0000175687F53545-F29645CE', 4, 'Angostury Bitters (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194000-00001756C298A1F6-4B3932A0', '20081130193959-0000175687F53545-F29645CE', 5, '1 wiœnia koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194000-00001756C2AECFD7-33D1FF7D', '20081130193959-0000175687F53545-F29645CE', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194015-0000175A246A21DA-0F0271D1', '20081130194014-00001759D6F9F7E3-51CC038D', 1, 'Wytrawny Wermut (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194015-0000175A2485E204-411F4A69', '20081130194014-00001759D6F9F7E3-51CC038D', 2, 'Creme de Cassis (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194015-0000175A249D1B42-0A931170', '20081130194014-00001759D6F9F7E3-51CC038D', 3, 'Kawa³ek skórki cytrynowej')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194015-0000175A24B36C0E-BA0CFDE0', '20081130194014-00001759D6F9F7E3-51CC038D', 4, 'Woda sodowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194015-0000175A24C9EAB0-67E9806A', '20081130194014-00001759D6F9F7E3-51CC038D', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194025-0000175C68354F0E-F3EDBD31', '20081130194024-0000175C29ADE0F3-D55A05FE', 1, 'Gordon''s Dry Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194025-0000175C6851B401-269E98F8', '20081130194024-0000175C29ADE0F3-D55A05FE', 2, 'Likier Amaretto di Saronno (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194025-0000175C686B1110-69A21CE1', '20081130194024-0000175C29ADE0F3-D55A05FE', 3, 'Wermut Noilly Prat (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194025-0000175C68A002A3-AE180AC2', '20081130194024-0000175C29ADE0F3-D55A05FE', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194032-0000175E3B7C6153-3A9FBBD0', '20081130194031-0000175DF96B368A-4504CFDA', 1, 'Bia³y Rum (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194032-0000175E3B9A214C-AEECC108', '20081130194031-0000175DF96B368A-4504CFDA', 2, 'Likier Triple Lime (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194032-0000175E3BB27BB9-E51F5E18', '20081130194031-0000175DF96B368A-4504CFDA', 3, 'Orange Bitters (15 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194032-0000175E3BC900E7-B005B118', '20081130194031-0000175DF96B368A-4504CFDA', 4, 'Sok ananasowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194032-0000175E3BDEE493-A8A7A014', '20081130194031-0000175DF96B368A-4504CFDA', 5, 'Sok grejpfrutowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194032-0000175E3C226933-1010E4CB', '20081130194031-0000175DF96B368A-4504CFDA', 6, 'Sok cytrynowy (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194032-0000175E3C3D7CF2-02987460', '20081130194031-0000175DF96B368A-4504CFDA', 7, '1 bia³ko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194032-0000175E3C5480B7-E57FE438', '20081130194031-0000175DF96B368A-4504CFDA', 8, 'trochê t³uczonego lodu.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194041-000017603C6EAED6-C58037B9', '20081130194040-0000175FFCA77C34-36E76746', 1, 'Bia³y Rum (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194041-000017603C8B9E61-03A7156B', '20081130194040-0000175FFCA77C34-36E76746', 2, 'Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194041-000017603CA21D02-AAFA409B', '20081130194040-0000175FFCA77C34-36E76746', 3, 'Likier pomarañczowy (bia³y) - (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194041-000017603CB8F637-C9190F34', '20081130194040-0000175FFCA77C34-36E76746', 4, 'Likier any¿owy (1 barowa ³y¿ka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194041-000017603CCFE99D-F4B3D79F', '20081130194040-0000175FFCA77C34-36E76746', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194050-0000176246A7D0AD-C3FC381B', '20081130194049-000017620009C4B9-CE4FAD4A', 1, 'Wódka czysta (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194050-0000176246D9699A-C1AB1487', '20081130194049-000017620009C4B9-CE4FAD4A', 2, 'Likier kawowy (najlepiej Kahlua) - (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194050-0000176246F2E765-AA074903', '20081130194049-000017620009C4B9-CE4FAD4A', 3, 'S³odka skondensowana œmietanka (15 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194050-000017624709AE0D-0A80F475', '20081130194049-000017620009C4B9-CE4FAD4A', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194057-000017640AB1E2DD-A87E0453', '20081130194056-00001763B3B12255-FBEEA26B', 1, 'Likieru Tia Maria (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194057-000017640ACE688D-FD941D18', '20081130194056-00001763B3B12255-FBEEA26B', 2, 'Wódka (1miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194057-000017640AE56E80-7425C968', '20081130194056-00001763B3B12255-FBEEA26B', 3, 'Sprite')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194057-000017640AFC54CE-91C03BD8', '20081130194056-00001763B3B12255-FBEEA26B', 4, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194106-00001766261E19D6-6E18FB10', '20081130194105-00001765D87DCC29-E40EA2D8', 1, 'Whisky (4 miarki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194106-00001766263A80F7-108EB044', '20081130194105-00001765D87DCC29-E40EA2D8', 2, 'Wiœniówka (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194106-00001766265133FB-D4344A01', '20081130194105-00001765D87DCC29-E40EA2D8', 3, 'Likier Marasquino (2 miarki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194106-0000176626682760-A0221B42', '20081130194105-00001765D87DCC29-E40EA2D8', 4, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194106-00001766267DAB04-E35D1320', '20081130194105-00001765D87DCC29-E40EA2D8', 5, 'Woda sodowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194106-0000176626B1C36D-5A527B45', '20081130194105-00001765D87DCC29-E40EA2D8', 6, 'Pomidorki czereœniowe.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194114-00001767CEB7A9B0-8404FBD0', '20081130194113-0000176793092205-2CEDE19E', 1, 'Szampan Bollinger')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194114-00001767CECF96FD-721BC8FC', '20081130194113-0000176793092205-2CEDE19E', 2, 'Likier pomarañczowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194114-00001767CEE5DCE0-EA2E5028', '20081130194113-0000176793092205-2CEDE19E', 3, 'Brinen-Branntwein (wódka gruszkowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194114-00001767CEFBBD45-C1D8CFBE', '20081130194113-0000176793092205-2CEDE19E', 4, 'wytrawna')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194114-00001767CF11BF7F-D34C3557', '20081130194113-0000176793092205-2CEDE19E', 5, 'bezbarwna) - (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194114-00001767CF27A0FD-BF9ADA5D', '20081130194113-0000176793092205-2CEDE19E', 6, 'Cukier puder (1 barowa ³y¿ka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194114-00001767CF501523-6D2EF336', '20081130194113-0000176793092205-2CEDE19E', 7, 'Sok z 1/2 cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194114-00001767CF68DFF7-C07F5488', '20081130194113-0000176793092205-2CEDE19E', 8, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194121-00001769A053D374-D9FA7C99', '20081130194120-000017696A9BE163-53633ACF', 1, 'Wino musuj¹ce')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194121-00001769A070806D-69D8F40E', '20081130194120-000017696A9BE163-53633ACF', 2, 'Liker Amaretto (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194121-00001769A0877EBC-DA18A522', '20081130194120-000017696A9BE163-53633ACF', 3, 'Sok bananowy (60 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194121-00001769A0A093C5-CE1397A1', '20081130194120-000017696A9BE163-53633ACF', 4, 'Sok cytrynowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194121-00001769A0B871CA-E0955C10', '20081130194120-000017696A9BE163-53633ACF', 5, 'Wiœnia koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194121-00001769A0CF169E-797E93B6', '20081130194120-000017696A9BE163-53633ACF', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194129-0000176B8320361B-D09D46B3', '20081130194128-0000176B48267503-0F236CA2', 1, 'Likier any¿owy Zachêta (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194129-0000176B8338693F-64AEAA03', '20081130194128-0000176B48267503-0F236CA2', 2, 'Wino bia³e wytrawne (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194129-0000176B835DBB50-A4C059FC', '20081130194128-0000176B48267503-0F236CA2', 3, 'Sok grapefruitowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194129-0000176B8376FE2E-4B392D8E', '20081130194128-0000176B48267503-0F236CA2', 4, 'Owoce (jab³ko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194129-0000176B838D8473-E7CFC0E4', '20081130194128-0000176B48267503-0F236CA2', 5, 'truskawka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194129-0000176B83A3BB0F-EE6C038D', '20081130194128-0000176B48267503-0F236CA2', 6, 'kiwi')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194129-0000176B83B9CB79-C2C0C0A5', '20081130194128-0000176B48267503-0F236CA2', 7, 'pomarañcza)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194129-0000176B83D0115B-9BCF94C3', '20081130194128-0000176B48267503-0F236CA2', 8, 'Kostka lodu')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194129-0000176B83E620AE-83BF2E31', '20081130194128-0000176B48267503-0F236CA2', 9, 'Cukier.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194137-0000176D56C59F45-7FD9C2A5', '20081130194136-0000176D22312A3F-3A5265C5', 1, 'Likier Z³ota £¹ka (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194137-0000176D56EC2A89-81E8640E', '20081130194136-0000176D22312A3F-3A5265C5', 2, 'Wytrawny Szampan (30 ml).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194231-00001779EF70F5B6-45C8113C', '20081130194230-00001779B6C7DF28-F0F05128', 1, 'Sok ananasowy (75 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194231-00001779EF8BE115-0F9CFFF3', '20081130194230-00001779B6C7DF28-F0F05128', 2, 'Sok pomarañczowy (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194231-00001779EFA2952F-8D4DDBA7', '20081130194230-00001779B6C7DF28-F0F05128', 3, 'Sok z mango (35 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194231-00001779EFB87B0A-1CF48F77', '20081130194230-00001779B6C7DF28-F0F05128', 4, 'Sok z cytryny (35 ml).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194239-0000177BCC983D4B-7EA68DCD', '20081130194238-0000177B8995D8A1-E4ECF3BF', 1, 'Niebieskie Curacao (bezalkoholowe) - (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194239-0000177BCCB3810E-84D84F57', '20081130194238-0000177B8995D8A1-E4ECF3BF', 2, 'Sok ananasowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194239-0000177BCCCA4F59-61CE7FD8', '20081130194238-0000177B8995D8A1-E4ECF3BF', 3, 'Sok pomarañczowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194239-0000177BCD253480-394CAC07', '20081130194238-0000177B8995D8A1-E4ECF3BF', 4, 'S³odka œmietanka (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194239-0000177BCD3F4486-6F89D270', '20081130194238-0000177B8995D8A1-E4ECF3BF', 5, 'Sok cytrynowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194239-0000177BCD55D7E4-8D4574DF', '20081130194238-0000177B8995D8A1-E4ECF3BF', 6, '1 ¿ó³tko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194239-0000177BCD6C59CC-71CC2E55', '20081130194238-0000177B8995D8A1-E4ECF3BF', 7, 'Ananas (kawa³ek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194239-0000177BCD8296F3-1E4298DD', '20081130194238-0000177B8995D8A1-E4ECF3BF', 8, '1 wiœnia koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194239-0000177BCD990CDA-0EE20478', '20081130194238-0000177B8995D8A1-E4ECF3BF', 9, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194247-0000177DA7E6C5F1-C9C116E1', '20081130194246-0000177D696343CE-BDB2CFFC', 1, 'Bradny (50ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194247-0000177DA8270590-0DA41480', '20081130194246-0000177D696343CE-BDB2CFFC', 2, 'Cukier puder (1 barowa ³y¿eczka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194247-0000177DA83F53FC-F6396185', '20081130194246-0000177D696343CE-BDB2CFFC', 3, 'T³uczony lód.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194256-0000177F9C2FB04F-7CFFCFF7', '20081130194254-0000177F4EF1D00F-6DB56404', 1, 'Sok z pomarañczy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194256-0000177F9C4C26B8-4D0E1DD6', '20081130194254-0000177F4EF1D00F-6DB56404', 2, 'Sok z ananasa (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194256-0000177F9C63344E-DCFF07F1', '20081130194254-0000177F4EF1D00F-6DB56404', 3, 'Sok z granatów (25 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194256-0000177F9C7A2357-7C1CFA19', '20081130194254-0000177F4EF1D00F-6DB56404', 4, 'Sok z cytryny (25 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194256-0000177F9C90A310-11D73412', '20081130194254-0000177F4EF1D00F-6DB56404', 5, 'Gejpfrut (1 plasterek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194304-000017817B601F49-7CB71FEB', '20081130194303-000017814315D693-2AEA3EC7', 1, 'Sok z cytryny (25 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194304-000017817B7D0618-26C517F1', '20081130194303-000017814315D693-2AEA3EC7', 2, 'Syrop miêtowy (35 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194304-000017817BC4570B-406C0946', '20081130194303-000017814315D693-2AEA3EC7', 3, 'Tonik')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194304-000017817BDF7C40-EA8727F6', '20081130194303-000017814315D693-2AEA3EC7', 4, 'Cytryna lub limonka (1 plasterek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194311-0000178338267B4D-F2A3C861', '20081130194310-00001782FCFEE4E0-3A8189B5', 1, '1 ¿ó³tko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194311-000017833842AF25-9EA1B5D0', '20081130194310-00001782FCFEE4E0-3A8189B5', 2, 'Syrop grenadynowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194311-000017833859B1D2-671951C2', '20081130194310-00001782FCFEE4E0-3A8189B5', 3, 'Sok cytrynowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194311-000017833870235B-F97BE4FA', '20081130194310-00001782FCFEE4E0-3A8189B5', 4, 'Sok ananasowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194311-0000178338861994-BB80C37C', '20081130194310-00001782FCFEE4E0-3A8189B5', 5, 'Sok pomarañczowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194311-00001783389C6832-635D429E', '20081130194310-00001782FCFEE4E0-3A8189B5', 6, 'Pomarañcz (1/2 plastra)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194311-0000178338B6B89B-6D171A75', '20081130194310-00001782FCFEE4E0-3A8189B5', 7, 'Ananas (1 kawa³ek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194311-000017833906346D-59F54C3A', '20081130194310-00001782FCFEE4E0-3A8189B5', 8, '2 zielone wiœnie koktajlowe')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194311-00001783391DC09A-F8C29121', '20081130194310-00001782FCFEE4E0-3A8189B5', 9, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194320-000017854EEF5E7B-AA536ACC', '20081130194319-00001784F287EF6F-28939630', 1, 'Sok cytrynowy (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194320-000017854F0BB1F8-6A63C34E', '20081130194319-00001784F287EF6F-28939630', 2, '1 ¿ó³tko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194320-000017854F222B25-8E48DD3A', '20081130194319-00001784F287EF6F-28939630', 3, 'Cukier puder (2 barowe ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194320-000017854F39491A-CB932551', '20081130194319-00001784F287EF6F-28939630', 4, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194320-000017854F56C799-FC7E4E7F', '20081130194319-00001784F287EF6F-28939630', 5, 'Woda sodowa.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194333-00001788613BA0BB-4CDE23D7', '20081130194331-00001787E24E02E4-5B7E1590', 1, 'Sok z grejpfruta (100 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194333-000017886158A0A4-88D534C8', '20081130194331-00001787E24E02E4-5B7E1590', 2, 'Sok z granatów (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194333-000017886188850F-63A077E7', '20081130194331-00001787E24E02E4-5B7E1590', 3, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194333-0000178861A142CA-ECAC59EC', '20081130194331-00001787E24E02E4-5B7E1590', 4, 'Woda mineralna.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194341-0000178A3286510D-7A96353E', '20081130194340-00001789F565FFE4-9DAD1AA0', 1, 'Whiskey Jack Daniel''s (40ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194341-0000178A32A33267-98404525', '20081130194340-00001789F565FFE4-9DAD1AA0', 2, 'Coca cola (150ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194341-0000178A32B98E1D-B3534406', '20081130194340-00001789F565FFE4-9DAD1AA0', 3, 'Lód (3 kostki).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194349-0000178BF93AF907-D80FF7C2', '20081130194348-0000178BBEC69797-265254DB', 1, 'Sok cytrynowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194349-0000178BF9574B6D-7DE15B7D', '20081130194348-0000178BBEC69797-265254DB', 2, 'Cukier puder (4 barowe ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194349-0000178BFA959F1A-4F19989F', '20081130194348-0000178BBEC69797-265254DB', 3, '1 ¿ó³tko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194349-0000178BFAAFF4F8-68FC4212', '20081130194348-0000178BBEC69797-265254DB', 4, 'Czerwony cukier')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194349-0000178BFAC67D6C-9A991CFE', '20081130194348-0000178BBEC69797-265254DB', 5, 'Sok cytrynowy do zwil¿enia kieliszka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194349-0000178BFADC6C02-A839CEE4', '20081130194348-0000178BBEC69797-265254DB', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194357-0000178DECF08EEC-ACB56D45', '20081130194356-0000178DA74CF889-23F4DE47', 1, 'Ogórki (0')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194357-0000178DED0CB494-F664D36A', '20081130194356-0000178DA74CF889-23F4DE47', 2, '40 kg)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194357-0000178DED23AB40-D96AFF09', '20081130194356-0000178DA74CF889-23F4DE47', 3, 'Sok z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194357-0000178DED39E2F3-2328D340', '20081130194356-0000178DA74CF889-23F4DE47', 4, 'Cytryna (2 plasterki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194357-0000178DED5005EA-FC81F0AF', '20081130194356-0000178DA74CF889-23F4DE47', 5, 'Miód')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194357-0000178DED65F596-D750A85E', '20081130194356-0000178DA74CF889-23F4DE47', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194406-000017900C541493-ACA2AF4A', '20081130194406-0000178FDD3441CA-C00BB6FA', 1, 'Pomidory (1/4 kg)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194406-000017900C70786F-55BF239F', '20081130194406-0000178FDD3441CA-C00BB6FA', 2, '£odyga selera naciowego')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194406-000017900C870540-57470EF3', '20081130194406-0000178FDD3441CA-C00BB6FA', 3, 'Sok z 1/4 cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194406-000017900CB87A2A-C042973E', '20081130194406-0000178FDD3441CA-C00BB6FA', 4, 'Sól')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194406-000017900CD2AC05-47AC0FC6', '20081130194406-0000178FDD3441CA-C00BB6FA', 5, 'Pieprz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194406-000017900CE97939-CA640BEF', '20081130194406-0000178FDD3441CA-C00BB6FA', 6, 'Sos tabasco')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194406-000017900CFFC262-8765AF82', '20081130194406-0000178FDD3441CA-C00BB6FA', 7, 'Sos worcester')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194406-000017900D163502-A965EDDE', '20081130194406-0000178FDD3441CA-C00BB6FA', 8, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194406-000017900D2C2D6A-0B041642', '20081130194406-0000178FDD3441CA-C00BB6FA', 9, 'Posiekana natka.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194417-00001792787FA023-C0453242', '20081130194416-000017923C84270B-1B2C8B45', 1, 'Sok pomidorowy (60 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194417-00001792789C2E8E-E4EFD841', '20081130194416-000017923C84270B-1B2C8B45', 2, 'Czerwony ocet winny i Tabasco (po kilka kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194417-0000179278B34EB1-C66E3E73', '20081130194416-000017923C84270B-1B2C8B45', 3, 'Sos worcester (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194417-0000179278C967D6-E4ED5793', '20081130194416-000017923C84270B-1B2C8B45', 4, 'Pieprz')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194417-0000179278F6AF4E-D86DE58E', '20081130194416-000017923C84270B-1B2C8B45', 5, '1 ¿ó³tko.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194424-000017941187A064-4EA7B75C', '20081130194423-00001793E0CE05D2-45050529', 1, 'Sok z maracuji (40 m)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194424-0000179411A3EB26-ABDC7A74', '20081130194423-00001793E0CE05D2-45050529', 2, 'Sok pomarañczowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194424-0000179411C7E349-599AA73D', '20081130194423-00001793E0CE05D2-45050529', 3, 'Sok cytrynowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194424-0000179411E21981-E0F065B6', '20081130194423-00001793E0CE05D2-45050529', 4, 'Syrop grenadynowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194424-0000179411F8F087-E3064A2E', '20081130194423-00001793E0CE05D2-45050529', 5, 'S³odka œmietanka (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194424-00001794120FA8FF-A327ABD7', '20081130194423-00001793E0CE05D2-45050529', 6, 'Angostury Bitters (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194424-000017941225E3F8-A8556BDF', '20081130194423-00001793E0CE05D2-45050529', 7, '1 ¿ó³tko')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194424-00001794123C95E4-EC88885A', '20081130194423-00001793E0CE05D2-45050529', 8, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194431-00001795CCFC83D6-FE43F368', '20081130194430-0000179574ECDAEA-8BA8B644', 1, 'Truskawki (160 g)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194431-00001795CD226A52-84CD78FD', '20081130194430-0000179574ECDAEA-8BA8B644', 2, 'Œmietanka (2/3 szklanki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194431-00001795CD39DB36-F5053395', '20081130194430-0000179574ECDAEA-8BA8B644', 3, 'Lód (kilka kostek)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194431-00001795CD50617C-61D026C2', '20081130194430-0000179574ECDAEA-8BA8B644', 4, 'Cukier do smaku.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194441-000017980AD25900-096741B2', '20081130194440-00001797CCCCB183-A0A0068B', 1, 'Sok z moreli (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194441-000017980AEE8762-62EEB424', '20081130194440-00001797CCCCB183-A0A0068B', 2, 'Sok z gruszek (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194441-000017980B05720D-584789A1', '20081130194440-00001797CCCCB183-A0A0068B', 3, 'Sok z czereœni (25 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194441-000017980B1C6115-F61B0C7C', '20081130194440-00001797CCCCB183-A0A0068B', 4, 'Sok z ciemnych winogron (12 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194441-000017980B32F473-48BD96E4', '20081130194440-00001797CCCCB183-A0A0068B', 5, 'Tonik')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194441-000017980B494CE3-59ACF8A9', '20081130194440-00001797CCCCB183-A0A0068B', 6, 'Cytryna (plasterek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194654-000017B70F883145-9BF17356', '20081130194653-000017B6E06ADAB2-3BE10266', 1, 'Campari Bitter (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194654-000017B70FE01513-56C4F698', '20081130194653-000017B6E06ADAB2-3BE10266', 2, 'Czerwony Wermut (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194654-000017B70FF89D56-0F51E43D', '20081130194653-000017B6E06ADAB2-3BE10266', 3, 'Woda sodowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194654-000017B71012F791-FD82A648', '20081130194653-000017B6E06ADAB2-3BE10266', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194703-000017B91C30E8A1-9EC7DF7F', '20081130194701-000017B8CFFB3F7A-80E0D45B', 1, 'Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194703-000017B91C4C0BA8-FA6536FB', '20081130194701-000017B8CFFB3F7A-80E0D45B', 2, 'Czerwony Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194703-000017B91C625FBA-F976BBF4', '20081130194701-000017B8CFFB3F7A-80E0D45B', 3, 'Sok pomarañczowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194703-000017B91C7EFE83-9133BBE5', '20081130194701-000017B8CFFB3F7A-80E0D45B', 4, 'Angostury Bitters (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194703-000017B91D1A93AF-0698F47B', '20081130194701-000017B8CFFB3F7A-80E0D45B', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194711-000017BAFBF307F1-88180533', '20081130194709-000017BAABE1077A-371C5307', 1, 'Bia³y Rum Bacardi (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194711-000017BAFC0E306C-E76E5E31', '20081130194709-000017BAABE1077A-371C5307', 2, 'Wytrawny Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194711-000017BAFC4EF75C-4E3AE5C1', '20081130194709-000017BAABE1077A-371C5307', 3, 'Zielona oliwka')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194711-000017BAFCAC485A-E0761F9F', '20081130194709-000017BAABE1077A-371C5307', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194730-000017BF84364A72-2439E714', '20081130194729-000017BF470BBD25-55DF8D17', 1, 'Piwo Ginger Ale')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194730-000017BF844FE7E3-3EC07766', '20081130194729-000017BF470BBD25-55DF8D17', 2, 'Gordon''s Dry Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194730-000017BF84658B2C-A9BFE2F6', '20081130194729-000017BF470BBD25-55DF8D17', 3, 'Wytrawny Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194730-000017BF847A0F74-22DA5019', '20081130194729-000017BF470BBD25-55DF8D17', 4, 'Brandy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194730-000017BF85272104-CF57C755', '20081130194729-000017BF470BBD25-55DF8D17', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194739-000017C1A9B95643-1E73957E', '20081130194738-000017C16B4E445F-8C45FC7D', 1, 'Gin (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194739-000017C1A9D2BF52-D23BF2F8', '20081130194738-000017C16B4E445F-8C45FC7D', 2, 'Bia³y Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194739-000017C1A9E7D28F-406C6918', '20081130194738-000017C16B4E445F-8C45FC7D', 3, 'Niebieskie Curacao (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194739-000017C1A9FCB7F7-3371462C', '20081130194738-000017C16B4E445F-8C45FC7D', 4, 'Tonik')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194739-000017C1AA9EF6CB-446C5175', '20081130194738-000017C16B4E445F-8C45FC7D', 5, 'Owoce do dekoracji')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194739-000017C1AABC61A5-1F1E8431', '20081130194738-000017C16B4E445F-8C45FC7D', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194759-000017C647A48AD9-417F1915', '20081130194758-000017C5DE0C6532-2030627D', 1, 'Campari (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194759-000017C647C0135C-051AC073', '20081130194758-000017C5DE0C6532-2030627D', 2, 'Sok cytrynowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194759-000017C64835F9B4-77805BDF', '20081130194758-000017C5DE0C6532-2030627D', 3, 'Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194759-000017C648712371-3D628759', '20081130194758-000017C5DE0C6532-2030627D', 4, 'Sok pomarañczowy (80 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194759-000017C64887803E-784C976B', '20081130194758-000017C5DE0C6532-2030627D', 5, 'Lód (2-3 kostki).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194809-000017C8A34499A6-CA2FD88E', '20081130194808-000017C8329EEAC7-E2F43921', 1, 'Wytrawny Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194809-000017C8A3BAE692-F851B325', '20081130194808-000017C8329EEAC7-E2F43921', 2, 'Gordon''s Dry Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194809-000017C8A3F41B6A-DAB3B8EA', '20081130194808-000017C8329EEAC7-E2F43921', 3, 'Campari Bitter (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194809-000017C8A40CE2F8-788809AC', '20081130194808-000017C8329EEAC7-E2F43921', 4, 'Skórka cytrynowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194809-000017C8A422D2A4-F3DA2E12', '20081130194808-000017C8329EEAC7-E2F43921', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194817-000017CA7DE6FC73-6664A1E6', '20081130194817-000017CA4AC65FFF-3D9EFE43', 1, 'Cherry (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194817-000017CA7DFDF31F-33256901', '20081130194817-000017CA4AC65FFF-3D9EFE43', 2, 'Wytrawny Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194817-000017CA7E1057F4-CF056DA5', '20081130194817-000017CA4AC65FFF-3D9EFE43', 3, 'Czerwony Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194817-000017CA7E211BEB-29C8F151', '20081130194817-000017CA4AC65FFF-3D9EFE43', 4, 'Woda mineralna (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194817-000017CA7E323A75-4821A28A', '20081130194817-000017CA4AC65FFF-3D9EFE43', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194836-000017CEBCAD7F59-755E9265', '20081130194834-000017CE6C6FFD62-45AE293F', 1, 'Gordon''s Dry Gin (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194836-000017CEBCC74EFD-C632496B', '20081130194834-000017CE6C6FFD62-45AE293F', 2, 'Wytrawny Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194836-000017CEBCDD4CD9-F0FA63AE', '20081130194834-000017CE6C6FFD62-45AE293F', 3, 'Wódka any¿owa Pastis (25 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194836-000017CEBCF27C76-2AD458AF', '20081130194834-000017CE6C6FFD62-45AE293F', 4, 'Angostury Bitters (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194836-000017CEBD07A358-5E04AF2D', '20081130194834-000017CE6C6FFD62-45AE293F', 5, 'Pomarañcz (1/2 plastra)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194836-000017CEBD1C4974-4395CBC0', '20081130194834-000017CE6C6FFD62-45AE293F', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194920-000017D90A2684DD-F26D8D26', '20081130194919-000017D8CA2F00AF-4A0BB540', 1, 'Bourbon (2 miarki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194920-000017D90A426396-AF58C467', '20081130194919-000017D8CA2F00AF-4A0BB540', 2, 'S³odki czerwony Wermut (np. Cinzano) - (1 miarka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194920-000017D90A59B704-ABA73B95', '20081130194919-000017D8CA2F00AF-4A0BB540', 3, 'Angostury Bitters (gorzka pomarañczówka) - (1/2 ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194920-000017D90A701EBB-EB2990AE', '20081130194919-000017D8CA2F00AF-4A0BB540', 4, 'kilka kropli Curacao')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194920-000017D90A861A69-178DAA6D', '20081130194919-000017D8CA2F00AF-4A0BB540', 5, 'Lód (3 kostki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194920-000017D90A9C4CA7-49E8651F', '20081130194919-000017D8CA2F00AF-4A0BB540', 6, 'wiœnia koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194920-000017D90AB1ECAB-CB03B618', '20081130194919-000017D8CA2F00AF-4A0BB540', 7, 'Skórka z cytryny.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194940-000017DDCE0C92C6-0024AFA9', '20081130194939-000017DD957D660D-4D8EF4E9', 1, 'Gin (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194940-000017DDCE45FBFF-F568628C', '20081130194939-000017DD957D660D-4D8EF4E9', 2, 'Czerwony Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194940-000017DDCE5E4725-DCEA5938', '20081130194939-000017DD957D660D-4D8EF4E9', 3, 'Angostury Bitters (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130194940-000017DDCE74E9CA-FD9BD71E', '20081130194939-000017DD957D660D-4D8EF4E9', 4, '1 wiœnia koktajlowa.')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195008-000017E43ACCA6B9-FB0DD908', '20081130195007-000017E3F6331851-5A9A930C', 1, 'Gin (50 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195008-000017E43AE9829F-2A191A52', '20081130195007-000017E3F6331851-5A9A930C', 2, 'Czerwony s³odki Wermut (Martini Rosso) - (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195008-000017E43B023113-768CA3DB', '20081130195007-000017E3F6331851-5A9A930C', 3, 'Skórka z pomarañczy')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195008-000017E43B1929EE-707C378B', '20081130195007-000017E3F6331851-5A9A930C', 4, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195015-000017E5F5801EE3-A8A4C8EB', '20081130195014-000017E5B70CAED6-CB6BB1CA', 1, 'Campari Bitter (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195015-000017E5F5CD53F8-1B28526A', '20081130195014-000017E5B70CAED6-CB6BB1CA', 2, 'Czerwony Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195015-000017E5F6222C9B-F1F380FF', '20081130195014-000017E5B70CAED6-CB6BB1CA', 3, 'Gordon''s Dry Gin (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195015-000017E5F63BC03A-445B51E0', '20081130195014-000017E5B70CAED6-CB6BB1CA', 4, 'kawa³ek skórki cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195015-000017E5F6535DDD-FCF41775', '20081130195014-000017E5B70CAED6-CB6BB1CA', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195035-000017EA85B26BE3-B4D5C4AF', '20081130195034-000017EA40E2EEBB-C466CDC4', 1, 'Gordon''s Dry Gin (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195035-000017EA86013A32-D0A5BA13', '20081130195034-000017EA40E2EEBB-C466CDC4', 2, 'Wytrawny Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195035-000017EA8619A844-2B668198', '20081130195034-000017EA40E2EEBB-C466CDC4', 3, 'Czerwony Wermut (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195035-000017EA862FD50D-FE938FAF', '20081130195034-000017EA40E2EEBB-C466CDC4', 4, 'Skórka z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195035-000017EA8646400A-1769EE80', '20081130195034-000017EA40E2EEBB-C466CDC4', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195055-000017EF3E43AE08-F632EF74', '20081130195054-000017EF07DE002B-939F7C3D', 1, 'Kirschwasser (wódka wiœniowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195055-000017EF3E601759-96196102', '20081130195054-000017EF07DE002B-939F7C3D', 2, 'bezbarwna i wytrawna) - (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195055-000017EF3E76ED48-072999FB', '20081130195054-000017EF07DE002B-939F7C3D', 3, 'Wytrawny Wermut (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195055-000017EF3E8D7902-8D224C15', '20081130195054-000017EF07DE002B-939F7C3D', 4, 'Syrop grenadynowy (15 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195055-000017EF3EC76DEA-9E5B6FAB', '20081130195054-000017EF07DE002B-939F7C3D', 5, '1 wiœnia koktajlowa')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195055-000017EF3EE5EAAD-32498FB6', '20081130195054-000017EF07DE002B-939F7C3D', 6, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195105-000017F1797473B1-4C82232D', '20081130195104-000017F149DAB7AD-A8FB272C', 1, 'Bia³y Wermut (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195105-000017F179B26B7B-6C3DBF6C', '20081130195104-000017F149DAB7AD-A8FB272C', 2, 'Czerwony Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195105-000017F179CBBEB8-1BE98D07', '20081130195104-000017F149DAB7AD-A8FB272C', 3, 'Gin (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195105-000017F179E20D55-5149303D', '20081130195104-000017F149DAB7AD-A8FB272C', 4, 'Campari Bitter (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195105-000017F179F89F9C-7DD2ACCE', '20081130195104-000017F149DAB7AD-A8FB272C', 5, 'Syrop z czarnej porzeczki (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195105-000017F17A0F1D26-5A7E68B2', '20081130195104-000017F149DAB7AD-A8FB272C', 6, 'Woda sodowa (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195105-000017F17A25B710-AFFECF87', '20081130195104-000017F149DAB7AD-A8FB272C', 7, 'Pomarañcz (1 plaster)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195105-000017F17A3BD034-93CB7F4D', '20081130195104-000017F149DAB7AD-A8FB272C', 8, 'Limetka (1 plaster)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195105-000017F17A529C51-51ED5F23', '20081130195104-000017F149DAB7AD-A8FB272C', 9, 'Miêta (1 ga³¹zka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195105-000017F17A68B68D-FD2C5E8D', '20081130195104-000017F149DAB7AD-A8FB272C', 10, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195113-000017F363E533F8-DF2A76CC', '20081130195112-000017F326748CBD-ACFD33B4', 1, 'Calvados Morin (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195113-000017F36401865E-BF37E7D2', '20081130195112-000017F326748CBD-ACFD33B4', 2, 'Czerwony Wermut (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195113-000017F3641848A9-52BA5646', '20081130195112-000017F326748CBD-ACFD33B4', 3, 'Angostury Bitter (8 kropli)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195113-000017F3642EAD1A-4E24232D', '20081130195112-000017F326748CBD-ACFD33B4', 4, 'Skórka z cytryny')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195113-000017F36445358E-65F695EE', '20081130195112-000017F326748CBD-ACFD33B4', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195155-000017FD36FC4FA0-75080F24', '20081130195154-000017FCFE310289-6A06FA20', 1, 'Calvados (1/12 szklanki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195155-000017FD3718E6C5-688AEF3E', '20081130195154-000017FCFE310289-6A06FA20', 2, 'Szampan (1/6 butelki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195155-000017FD372FCBFB-F831C451', '20081130195154-000017FCFE310289-6A06FA20', 3, 'Jab³ko (1/3)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195155-000017FD374634CA-62A606F4', '20081130195154-000017FCFE310289-6A06FA20', 4, 'Sok z cytryny (1/3 ³y¿eczki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195155-000017FD375C9C81-8F6BA7F7', '20081130195154-000017FCFE310289-6A06FA20', 5, 'Sok jab³kowy zimny (1/6 l).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195203-000017FF118B6CCB-A34D6F23', '20081130195202-000017FEDB04681B-7551270E', 1, 'Calvados (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195203-000017FF11A7C7EC-4F5C27E6', '20081130195202-000017FEDB04681B-7551270E', 2, 'Sok z cytryny (³y¿ka)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195203-000017FF11BE4002-FD2DA826', '20081130195202-000017FEDB04681B-7551270E', 3, 'Sok jab³kowy (1/2 szklanki)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195203-000017FF11D484CD-8FF701AA', '20081130195202-000017FEDB04681B-7551270E', 4, 'Sok grejpfrutowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195203-000017FF11F685DD-A39A432C', '20081130195202-000017FEDB04681B-7551270E', 5, 'Piwo imbirowe (20 ml).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195211-00001800DCE20C73-751402ED', '20081130195210-000018008ED7484F-9AF7BEE7', 1, 'Calvados Morin (30 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195211-00001800DCFE5736-30F1D859', '20081130195210-000018008ED7484F-9AF7BEE7', 2, 'Benedictine (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195211-00001800DD20678C-351AD173', '20081130195210-000018008ED7484F-9AF7BEE7', 3, 'Pomarañczowe Curacao (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195211-00001800DD3965AA-59B9268F', '20081130195210-000018008ED7484F-9AF7BEE7', 4, 'Sok cytrynowy (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195211-00001800DD54B686-521E49B7', '20081130195210-000018008ED7484F-9AF7BEE7', 5, 'Lód (kilka kostek).')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195226-00001804728C7920-F357758F', '20081130195225-000018043166C92E-6FA5B7DE', 1, 'Calvados (40 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195226-0000180472A565C7-52316C5D', '20081130195225-000018043166C92E-6FA5B7DE', 2, 'Sok cytrynowy (20 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195226-0000180472BBCD7E-2B1A6CDC', '20081130195225-000018043166C92E-6FA5B7DE', 3, 'Grenadyna (10 ml)')
/
INSERT INTO DRINK_COMPOS (DNC_ID, DNC_DNK_ID, DNC_NO, DNC_COMPONENT) VALUES ('20081130195226-0000180472D1BE43-60022D34', '20081130195225-000018043166C92E-6FA5B7DE', 4, 'Lód pokruszony (3-4 kostki).')
/
