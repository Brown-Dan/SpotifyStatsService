## SpotiStatsService

SpotiStatsService is a simple web service that allows users to upload and query their Spotify streaming data.

### Instructions

1. Visit [SpotiStats Login](https://www.spotifystats.co.uk/login) to obtain a JWT token.
2. Provide the JWT token in the "Authorization" header of your requests.
3. Optionally, you can upload your streaming data from [SpotiStats Data Upload](https://www.spotifystats.co.uk/data/upload) using a multipart request. Ensure the file is in JSON format and is under 20MB. Name the file "streamingDataFile".

### Endpoints

- **Recent Tracks**: `/tracks/recent`
  - **Parameters**:
    - `limit`: Specifies the number of recent tracks. Default is 10, maximum is 50.
    - `createPlaylist`: Optional. Default is `false`.
    - `advanced`: Optional. Default is `false`.
- **Top Tracks**: `/tracks/top`
  - **Parameters**:
    - `limit`: Specifies the number of top tracks. Default is 10, maximum is 50.
    - `page`: Optional. Default is 1.
    - `createPlaylist`: Optional. Default is `false`.
    - `advanced`: Optional. Default is `false`.
- **Top Artists**: `/artists/top`
  - **Parameters**:
    - `limit`: Specifies the number of top artists. Default is 10, maximum is 50.
    - `page`: Optional. Default is 1.
    - `createPlaylist`: Optional. Default is `false`.
    - `advanced`: Optional. Default is `false`.
- **Data Upload**: `/data/upload`
  - **Multipart Request**:
    - Upload your Spotify streaming history JSON file (max 20MB) with the name "StreamingDataFile".
   
- **Tracks/get**
- **Artist/get** 

### Search

- **Endpoint**: `/search`

#### Ordering Options:

- `DATE_ASC`
- `DATE_DESC`
- `MS_STREAMED_ASC`
- `MS_STREAMED_DESC`
- `ARTIST_NAME_ASC`
- `ARTIST_NAME_DESC`
- `TRACK_NAME_ASC`
- `TRACK_NAME_DESC`

#### Filters:

- `startDate`
- `endDate`
- `orderBy`
- `platform`
- `country`
- `artist`
- `trackUri`
- `trackName`
- `album`
- `limit`
- `startTime`
- `endTime`
- `month`
- `dayOfTheWeek`
- `createPlaylist`
- `year`
