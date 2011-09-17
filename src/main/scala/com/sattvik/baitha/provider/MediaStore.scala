/*
 * Copyright © 2011 Sattvik Software & Technology Resources, Ltd. Co.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.sattvik.baitha.provider

import android.provider.MediaStore.Audio._
import android.provider.MediaStore.MediaColumns
import com.sattvik.baitha.database.TypedColumn

/** Container for all media provider columns. */
object MediaStore {
  /** Common columns for most media provider tables. */
  trait MediaTypedColumns extends BaseTypedColumns {
    /** The data stream for the file. */
    val Data = TypedColumn[String](MediaColumns.DATA)
    /** The time the file was added to the media provider.  Units are seconds
      * since 1970. */
    val DateAdded = TypedColumn[Long](MediaColumns.DATE_ADDED)
    /** The time the file was last modified.  Units are seconds since 1970. */
    val DateModified = TypedColumn[Long](MediaColumns.DATE_MODIFIED)
    /** The display name of the file. */
    val DisplayName = TypedColumn[String](MediaColumns.DISPLAY_NAME)
    /** The MIME type of the file. */
    val MimeType = TypedColumn[String](MediaColumns.MIME_TYPE)
    /** The size of the file in bytes. */
    val Size = TypedColumn[Long](MediaColumns.SIZE)
    /** The title of the content. */
    val Title = TypedColumn[String](MediaColumns.TITLE)
  }

  /** Container for all audio-based media columns. */
  object Audio {
    /** Columns representing an album. */
    trait AlbumTypedColumns {
      /** The album on which the audio file appears, if any. */
      val Album = TypedColumn[String](AlbumColumns.ALBUM)
      /** The ID for the album. */
      val AlbumId = TypedColumn[Int](AlbumColumns.ALBUM_ID)
      /** A non human readable key calculated from the `Album`, used for
        * searching, sorting and grouping. */
      val AlbumKey = TypedColumn[String](AlbumColumns.ALBUM_KEY)
      /** The artist whose songs appear on this album. */
      val Artist = TypedColumn[String](AlbumColumns.ARTIST)
      /** The year in which the earliest songs on this album were released.
        * This will often be the same as `LastYear`, but for compilations
        * albums they might differ. */
      val FirstYear = TypedColumn[Int](AlbumColumns.FIRST_YEAR)
      /** The year in which the lates songs on this album were released.  This
        * will often be the same as `FirstYear`, but for compilations albums
        * they might differ. */
      val LastYear = TypedColumn[Int](AlbumColumns.LAST_YEAR)
      /** The number of songs on this album. */
      val NumSongs = TypedColumn[Int](AlbumColumns.NUMBER_OF_SONGS)
      /** This column is available when getting album info via artist, and
        * indicates the number of songs on the album by the given artist. */
      val NumArtistSongs = TypedColumn[Int](
        AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST)
    }

    /** Columns representing an artist. */
    trait ArtistTypedColumns {
      /** The artist who created the audio file, if any. */
      val Artist = TypedColumn[String](ArtistColumns.ARTIST)
      /** A non human readable key calculated from the `Artist`, used for
        * searching, sorting and grouping. */
      val ArtistKey = TypedColumn[String](ArtistColumns.ARTIST_KEY)
      /** The number of albums in the database for the artist. */
      val NumAlbums = TypedColumn[Int](ArtistColumns.NUMBER_OF_ALBUMS)
      /** The number of tracks in the database for the artist. */
      val NumTracks = TypedColumn[Int](ArtistColumns.NUMBER_OF_TRACKS)
    }

    /** Columns for an audio file that show up in multiple tables. */
    trait AudioTypedColumns extends MediaTypedColumns {
      /** The album the audio is from, if any. */
      val Album = TypedColumn[String](AudioColumns.ALBUM)
      /** The ID of the album the audio is from, if any. */
      val AlbumId = TypedColumn[Long](AudioColumns.ALBUM_ID)
      /** A non human readable key calculated from `Album`, used for searching,
        * sorting, and grouping. */
      val AlbumKey = TypedColumn[String](AudioColumns.ALBUM_KEY)
      /** The artist who created the audio file, if any. */
      val Artist = TypedColumn[String](AudioColumns.ARTIST)
      /** The ID of the artist who created the audio file, if any. */
      val ArtistId = TypedColumn[Long](AudioColumns.ARTIST_ID)
      /** A non human readable key calculated from `Artist`, used for searching,
        * sorting, and grouping. */
      val ArtistKey = TypedColumn[String](AudioColumns.ARTIST_KEY)
      /** The composer who created the audio file, if any. */
      val Composer = TypedColumn[String](AudioColumns.COMPOSER)
      /** The duration of the audio file, in ms. */
      val Duration = TypedColumn[Long](AudioColumns.DURATION)
      /** If the audio may be an alarm. */
      val IsAlarm = TypedColumn[Boolean](AudioColumns.IS_ALARM)
      /** If the audio is music. */
      val IsMusic = TypedColumn[Boolean](AudioColumns.IS_MUSIC)
      /** If the audio may be a notification sound. */
      val IsNotification = TypedColumn[Boolean](AudioColumns.IS_NOTIFICATION)
      /** If the audio may be a ring tone. */
      val IsRingTone = TypedColumn[Boolean](AudioColumns.IS_RINGTONE)
      /** A non human readable key calculated from `Title`, used for searching,
        * sorting, and grouping. */
      val TitleKey = TypedColumn[String](AudioColumns.TITLE_KEY)
      /** The track number of this song on the album, if any. This number encodes
        * both the track number and the disc number. For multi-disc sets, this
        * number will be 1xxx for tracks on the first disc, 2xxx for tracks on the
        * second disc, etc. */
      val Track = TypedColumn[Int](AudioColumns.TRACK)
      /** The year the audio file was recorded, if any. */
      val Year = TypedColumn[Int](AudioColumns.YEAR)
    }

    /** Columns representing an audio genre. */
    trait GenreTypedColumns {
      /** The name of the genre. */
      val Name = TypedColumn[String](GenresColumns.NAME)
    }

    /** Columns representing a playlist. */
    trait PlaylistTypedColumns {
      /** The data stream for the file. */
      val Data = TypedColumn[String](PlaylistsColumns.DATA)
      /** The time the file was added to the media provider.  Units are seconds
        * since 1970. */
      val DateAdded = TypedColumn[Long](PlaylistsColumns.DATE_ADDED)
      /** The time the file was last modified.  Units are seconds since 1970. */
      val DateModified = TypedColumn[Long](PlaylistsColumns.DATE_MODIFIED)
      /** The name of the playlist. */
      val Name = TypedColumn[String](PlaylistsColumns.NAME)
    }
  }
}
