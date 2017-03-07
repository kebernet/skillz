/*
 *    Copyright (c) 2016 Robert Cooper
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.kebernet.skillz.util;

/**
 * Values for the built-in Amazon slot types.
 */
@SuppressWarnings("unused")
public abstract class AmazonSlotTypes {

    public static final String NUMBER = "AMAZON.NUMBER";
    public static final String DATE = "AMAZON.DATE";
    public static final String TIME = "AMAZON.TIME";
    public static final String DURATION = "AMAZON.DURATION";
    public static final String FOUR_DIGIT_NUMBER = "AMAZON.FOUR_DIGIT_NUMBER";
    @Deprecated
    public static final String LITERAL = "AMAZON.LITERAL";
    /**
     * Names of actors and actresses.
     */
    public static final String ACTOR = "AMAZON.Actor";
    /**
     * Geographical regions that are typically under the jurisdiction of a particular government
     */
    public static final String ADMINISTRATIVE_AREA = "AMAZON.AdministrativeArea";
    /**
     * Words describing the overall rating for an item.
     */
    public static final String AGGREGATE_RATING = "AMAZON.AggregateRating";
    /**
     * Names of a variety of airlines.
     */
    public static final String AIRLINE = "AMAZON.Airline";
    /**
     * Names of a variety of airports.
     */
    public static final String AIRPORT = "AMAZON.Airport";
    /**
     * Names of many different animals.
     */
    public static final String ANIMAL = "AMAZON.Animal";
    /**
     * Full names of artists. (music?)
     */
    public static final String ARTIST = "AMAZON.Artist";
    /**
     * Full names of athletes.
     */
    public static final String ATHLETE = "AMAZON.Athlete";
    /**
     * Full names of authors.
     */
    public static final String AUTHOR = "AMAZON.Author";
    /**
     * Titles of books.
     */
    public static final String BOOK = "AMAZON.Book";
    /**
     * Titles of multi-book series.
     */
    public static final String BOOK_SERIES = "AMAZON.BookSeries";
    /**
     * Names and abbreviations of broadcast channels, such as TV and ratio stations.
     */
    public static final String BROADCAST_CHANNEL ="AMAZON.BroadcastChannel";
    /**
     * Words and phrases describing public structures and facilities, such as “town hall”, “bus stop”, and others.
     */
    public static final String CIVIC_STRUCTURE = "AMAZON.CivicStructure";
    /**
     * Names of colors.
     */
    public static final String COLOR = "AMAZON.Color";
    /**
     * Titles of comic books.
     */
    public static final String COMIC = "AMAZON.Comic";
    /**
     * Full names of corporations.
     */
    public static final String CORPORATION = "AMAZON.Corporation";
    /**
     * Names of countries around the world.
     */
    public static final String COUNTRY = "AMAZON.Country";
    /**
     * Words for different types of creative works, such as “song” or “show”.
     */
    public static final String CREATIVE_WORK_TYPE = "AMAZON.CreativeWorkType";
    /**
     * Calendar days of the week.
     */
    public static final String DAY_OF_WEEK = "AMAZON.DayOfWeek";
    /**
     * Names of various desserts.
     */
    public static final String DESSERT = "AMAZON.Dessert";
    /**
     * Words for different types of devices, such as “laptop”.
     */
    public static final String DEVICE_TYPE = "AMAZON.DeviceType";
    /**
     * Full names of film directors.
     */
    public static final String DIRECTOR = "AMAZON.Director";
    /**
     * Names of beverages.
     */
    public static final String DRINK = "AMAZON.Drink";
    /**
     * Names of schools, colleges, and other educational institutions.
     */
    public static final String EDUCATIONAL_ORGANIZATION = "AMAZON.EducationalOrganization";
    /**
     * Words describing different types of events. (Calendar)
     */
    public static final String EVENT_TYPE = "AMAZON.EventType";
    /**
     * Names of festivals.
     */
    public static final String FESTIVAL = "AMAZON.Festival";
    /**
     * Names of fictional characters from books, movies, television shows, and other fictional works.
     */
    public static final String FICTIONAL_CHARACTER = "AMAZON.FictionalCharacter";
    /**
     * Names of businesses that provide financial services.
     */
    public static final String FINANCIAL_SERVICE = "AMAZON.FinancialService";
    /**
     * Names of food items.
     */
    public static final String FOOD = "AMAZON.Food";
    /**
     * Names of businesses that serve food.
     */
    public static final String FOOD_ESTABLISHMENT = "AMAZON.FoodEstablishment";
    /**
     * Names of many different games.
     */
    public static final String GAME = "AMAZON.Game";
    /**
     * Names of many different genres that can be used to describe music, books, television shows, and other media.
     */
    public static final String GENRE = "AMAZON.Genre";
    /**
     * Names of landforms such as mountains, plains, lakes, rivers, bays, peninsulas, and seas.
     */
    public static final String LANDFORM = "AMAZON.Landform";
    /**
     * Names of historical buildings and landmarks.
     */
    public static final String LANDMARKS_OR_HISTORICAL_BUILDINGS = "AMAZON.LandmarksOrHistoricalBuildings";
    /**
     * Natural languages such as Spanish, Tamil, Hindi, and English.
     */
    public static final String LANGUAGE = "AMAZON.Language";
    /**
     * Names of businesses.
     */
    public static final String LOCAL_BUSINESS = "AMAZON.LocalBusiness";
    /**
     * Words describing different types of businesses a user might search for.
     */
    public static final String LOCAL_BUSINESS_TYPE = "AMAZON.LocalBusinessType";
    /**
     * Names of medical organizations (physical or not) such as hospitals, institutions, or clinics.
     */
    public static final String MEDICAL_ORGANIZATION = "AMAZON.MedicalOrganization";
    /**
     * Names of calendar months.
     */
    public static final String MONTH = "AMAZON.Month";
    /**
     * Titles of movies.
     */
    public static final String MOVIE = "AMAZON.Movie";
    /**
     * Titles of several multi-movie series.
     */
    public static final String MOVIE_SERIES = "AMAZON.MovieSeries";
    /**
     * Names of movie theaters.
     */
    public static final String MOVIE_THEATER = "AMAZON.MovieTheater";
    /**
     * Names of music albums.
     */
    public static final String MUSIC_ALBUM = "AMAZON.MusicAlbum";
    /**
     * Words describing different types of musical works, such as songs and tracks.
     */
    public static final String MUSIC_CREATIVE_WORK_TYPE = "AMAZON.MusicCreativeWorkType";
    /**
     * Names of music-related events, such as music festivals and concerts.
     */
    public static final String MUSIC_EVENT = "AMAZON.MusicEvent";
    /**
     * Names of musical groups. Includes both individual performers and groups such as bands, orchestras, or choirs.
     */
    public static final String MUSIC_GROUP = "AMAZON.MusicGroup";
    /**
     * Full names of musicians.
     */
    public static final String MUSICIAN = "AMAZON.Musician";
    /**
     * Names commonly used to describe playlists for music.
     */
    public static final String MUSIC_PLAYLIST = "AMAZON.MusicPlaylist";
    /**
     * Titles of music recordings or tracks. Each title normally represents a single song.
     */
    public static final String MUSIC_RECORDING = "AMAZON.MusicRecording";
    /**
     * Names of venues that are used for musical performances.
     */
    public static final String MUSIC_VENUE = "AMAZON.MusicVenue";
    /**
     * Titles of music videos.
     */
    public static final String MUSIC_VIDEO = "AMAZON.MusicVideo";
    /**
     * Names of non-governmental organizations.
     */
    public static final String ORGANIZATION = "AMAZON.Organization";
    /**
     * Full names of real and fictional people.
     */
    public static final String PERSON = "AMAZON.Person";
    /**
     * Street addresses, consisting of the building or house number and street name.
     */
    public static final String POSTAL_ADDRESS = "AMAZON.PostalAddress";
    /**
     *  bill gates
     * isaac tichenor
     * james rice
     * michael jordan
     * stephen king
     */
    public static final String PROFESSIONAL = "AMAZON.Professional";
    /**
     * Words describing a variety of professions.
     */
    public static final String PROFESSIONAL_TYPE = "AMAZON.ProfessionalType";
    /**
     * Names of radio channels and programs.
     */
    public static final String RADIO_CHANNEL = "AMAZON.RadioChannel";
    /**
     * Names of well-known residences.
     */
    public static final String RESIDENCE = "AMAZON.Residence";
    /**
     * Names of rooms typical in houses and other buildings.
     */
    public static final String ROOM = "AMAZON.Room";
    /**
     * Names of events for screening films.
     */
    public static final String SCREENING_EVENT = "AMAZON.ScreeningEvent";
    /**
     * Names of services.
     */
    public static final String SERVICE = "AMAZON.Service";
    /**
     * Names of social media platforms.
     */
    public static final String SOCIAL_MEDIA_PLATFORM = "AMAZON.SocialMediaPlatform";
    /**
     * Names of software programs and apps.
     */
    public static final String SOFTWARE_APPLICATION = "AMAZON.SoftwareApplication";
    /**
     * Names of software games, such as quiz games, trivia games, puzzle games, word games, and other video games.
     */
    public static final String SOFTWARE_GAME = "AMAZON.SoftwareGame";
    /**
     * Names of sports.
     */
    public static final String SPORT = "AMAZON.Sport";
    /**
     * Names of sporting events.
     */
    public static final String SPORTS_EVENT = "AMAZON.SportsEvent";
    /**
     * Names of many sports teams.
     */
    public static final String SPORTS_TEAM = "AMAZON.SportsTeam";
    /**
     * The names of streets used within a typical street address. Note that these names just include the street name, not the house number.
     */
    public static final String STREET_ADDRESS = "AMAZON.StreetAddress";
    /**
     * Names and abbreviations for television channels.
     */
    public static final String TELEVISION_CHANNEL = "AMAZON.TelevisionChannel";
    /**
     * Titles of television episodes.
     */
    public static final String TELEVISION_EPISODE = "AMAZON.TVEpisode";
    /**
     * Names of seasons of television shows.
     */
    public static final String TELEVISION_SEASON = "AMAZON.TVSeason";
    /**
     * Titles of many television series.
     */
    public static final String TELEVISION_SERIES = "AMAZON.TVSeries";
    /**
     * Titles of video games.
     */
    public static final String VIDEO_GAME = "AMAZON.VideoGame";
    /**
     * Names of a variety of weather conditions, such as rain, cold, or humid.
     */
    public static final String WEATHER_CONDITION = "AMAZON.WeatherCondition";
    /**
     * Words describing written works, such as books and poems.
     */
    public static final String WRITTEN_CREATIVE_WORK_TYPE = "AMAZON.WrittenCreativeWorkType";


    /**
     * Austria focused slot types.
     */
    public static abstract class Austria {
        public static final String CITY = "AMAZON.AT_CITY";
        public static final String REGION = "AMAZON.AT_REGION";
    }

    /**
     * Germany focused slot types.
     */
    public static abstract class Germany {
        public static final String CITY = "AMAZON.DE_CITY";
        public static final String FIRST_NAME = "AMAZON.DE_FIRST_NAME";
        public static final String REGION = "AMAZON.DE_REGION";
    }

    /**
     * Europe focused slot types.
     */
    public static abstract class Europe {
        public static final String CITY = "AMAZON.EUROPE_CITY";
    }

    /**
     * United Kingdom focused slot types.
     */
    public static abstract class UnitedKingdom {
        public static final String CITY = "AMAZON.GB_CITY";
        public static final String FIRST_NAME = "AMAZON.GB_FIRST_NAME";
        public static final String REGION = "AMAZON.GB_REGION";
    }

    /**
     * United States focused slot types.
     */
    public static abstract class UnitedStates {
        public static final String CITY = "AMAZON.US_CITY";
        public static final String FIRST_NAME = "AMAZON.US_FIRST_NAME";
        public static final String STATE = "AMAZON.US_STATE";
    }


}
