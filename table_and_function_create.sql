-- Create tabel for data in PostgreSQL Database:

CREATE TABLE weather_video (
id serial,
create_datetime timestamp NULL,
orig_filename text null,
file_data bytea null
) WITH (OIDS=FALSE) ;

-- Save video file with plpython3u function to PostgreSQL Database:

CREATE OR REPLACE FUNCTION public.weather_video_radar()
RETURNS integer
LANGUAGE plpython3u
AS $function$
import psycopg2
import urllib3
http = urllib3.PoolManager()
r = http.request("GET", "http://www.idokep.hu/radar/radar.mp4?e41a3",
preload_content=False)
mp4file = r.read()
curs = psycopg2.connect(database="database_name", user="user_name,
password="password", host="0.0.0.0").cursor()
curs.execute("SET TIME ZONE 
"Europe/Budapest";")
curs.execute("truncate 
public.weather_video;")
curs.execute("commit;")
curs.execute("INSERT INTO 
weather_video(id,create_datetime, orig_filename,
file_data) VALUES 
(DEFAULT,CURRENT_TIMESTAMP,%s,%s) RETURNING id;",
("radar.mp4", psycopg2.Binary(mp4file)))
returned_id = curs.fetchone()[0]
curs.execute("commit;")
curs.close()
return returned_id
$function$
