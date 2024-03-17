# [WIP] SpotiStatsService
Simple web service enabling user's to perform advanced queries on their uploaded Spotify account data.

FINISHED 

www.spotifystats.co.uk/username/recent Params -> limit (default=10, max=50), createPlaylist(default=false) 
www.spotifystats.co.uk/username/top   Params -> limit (default=10, max=50), createPlaylist(default=false) 
www.spotifystats.co.uk/username/upload MultipartRequest -> upload spotify streaming history json file < 20mb with name "StreamingDataFile" 

<H1> SEARCH </H1> 

<H2> www.spotifystats.co.uk/username/search </H2>

<H3> filters <H3>

<ul>
  <li> startDate </li>
    <li> endDate </li>
  <li> orderBy </li>
  <li> platform </li>
  <li> country </li>
  <li> artist </li>
  <li> trackUri </li>
  <li> trackName </li>
  <li> artist </li>
  <li> album </li>
  <li> artist </li>
  <li> limit </li>
  <li> startTime </li>
  <li> endTime </li>
</ul>
