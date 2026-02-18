package com.mahiri.nazmi.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "nazmi.db"
        const val DATABASE_VERSION = 5

        const val TABLE_POETRY = "poetry"
        const val COL_POETRY_ID = "id"
        const val COL_POETRY_CONTENT = "content"
        const val COL_POETRY_CATEGORY_ID = "category_id"
        const val COL_POETRY_IS_FAVORITE = "is_favorite"
        const val COL_POETRY_IS_USER_ADDED = "is_user_added"

        const val TABLE_CATEGORY = "category"
        const val COL_CATEGORY_ID = "id"
        const val COL_CATEGORY_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_CATEGORY (" +
                    "$COL_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_CATEGORY_NAME TEXT NOT NULL UNIQUE);"
        )

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_POETRY (" +
                    "$COL_POETRY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_POETRY_CONTENT TEXT, " +
                    "$COL_POETRY_CATEGORY_ID INTEGER, " +
                    "$COL_POETRY_IS_FAVORITE INTEGER DEFAULT 0, " +
                    "$COL_POETRY_IS_USER_ADDED INTEGER DEFAULT 0, " +
                    "FOREIGN KEY($COL_POETRY_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($COL_CATEGORY_ID) ON DELETE SET NULL)"
        )

        seedCategories(db)
        seedPoems(db)
    }

    private fun seedCategories(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Love');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Nature');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Motivation');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Life');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Melancholy');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Friendship');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Spiritual');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Hope');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Reflection');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Courage');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Wisdom');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Peace');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Dreams');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Time');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('Beauty');")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_CATEGORY ($COL_CATEGORY_NAME) VALUES ('User Added');")
    }

    private fun seedPoems(db: SQLiteDatabase) {
        // Get category IDs
        val loveId = getCategoryId(db, "Love")
        val natureId = getCategoryId(db, "Nature")
        val motivationId = getCategoryId(db, "Motivation")
        val lifeId = getCategoryId(db, "Life")
        val melancholyId = getCategoryId(db, "Melancholy")
        val friendshipId = getCategoryId(db, "Friendship")
        val spiritualId = getCategoryId(db, "Spiritual")
        val hopeId = getCategoryId(db, "Hope")
        val reflectionId = getCategoryId(db, "Reflection")
        val courageId = getCategoryId(db, "Courage")
        val wisdomId = getCategoryId(db, "Wisdom")
        val peaceId = getCategoryId(db, "Peace")
        val dreamsId = getCategoryId(db, "Dreams")
        val timeId = getCategoryId(db, "Time")
        val beautyId = getCategoryId(db, "Beauty")

        // Seed poems for each category (at least 30 per category)
        seedPoemsForCategory(db, loveId, getLovePoems())
        seedPoemsForCategory(db, natureId, getNaturePoems())
        seedPoemsForCategory(db, motivationId, getMotivationPoems())
        seedPoemsForCategory(db, lifeId, getLifePoems())
        seedPoemsForCategory(db, melancholyId, getMelancholyPoems())
        seedPoemsForCategory(db, friendshipId, getFriendshipPoems())
        seedPoemsForCategory(db, spiritualId, getSpiritualPoems())
        seedPoemsForCategory(db, hopeId, getHopePoems())
        seedPoemsForCategory(db, reflectionId, getReflectionPoems())
        seedPoemsForCategory(db, courageId, getCouragePoems())
        seedPoemsForCategory(db, wisdomId, getWisdomPoems())
        seedPoemsForCategory(db, peaceId, getPeacePoems())
        seedPoemsForCategory(db, dreamsId, getDreamsPoems())
        seedPoemsForCategory(db, timeId, getTimePoems())
        seedPoemsForCategory(db, beautyId, getBeautyPoems())
    }

    private fun getCategoryId(db: SQLiteDatabase, categoryName: String): Long {
        val c = db.rawQuery("SELECT $COL_CATEGORY_ID FROM $TABLE_CATEGORY WHERE $COL_CATEGORY_NAME = ?", arrayOf(categoryName))
        try {
            if (c.moveToFirst()) {
                return c.getLong(0)
            }
        } finally {
            c.close()
        }
        return -1
    }

    private fun seedPoemsForCategory(db: SQLiteDatabase, categoryId: Long, poems: Array<String>) {
        if (categoryId == -1L) return
        for (poem in poems) {
            db.execSQL(
                "INSERT OR IGNORE INTO $TABLE_POETRY ($COL_POETRY_CONTENT, $COL_POETRY_CATEGORY_ID, $COL_POETRY_IS_FAVORITE, $COL_POETRY_IS_USER_ADDED) VALUES (?, ?, 0, 0)",
                arrayOf(poem, categoryId)
            )
        }
    }

    private fun getLovePoems(): Array<String> {
        return arrayOf(
            "In your eyes I find my home\nIn your smile I find my peace\nLove is the language we speak",
            "Your heart beats in rhythm with mine\nTwo souls entwined by time\nForever yours, forever mine",
            "Love is not about finding the perfect person\nBut seeing an imperfect person perfectly",
            "In quiet moments your presence fills the room\nEven when you're far away\nLove knows no distance",
            "Your touch ignites a thousand stars\nIn the darkness of my heart\nYou are my light",
            "Love is not possession\nIt is appreciation\nOf another soul's essence",
            "In your embrace I find completeness\nIn your love I find home\nYou are my everything",
            "Love is patient, love is kind\nLove is finding beauty in the ordinary\nTogether we are extraordinary",
            "Your laughter is my favorite melody\nYour smile is my favorite view\nLove is simply you",
            "When I look at you\nI see forever\nWritten in your eyes",
            "Love is not about grand gestures\nIt's about the small moments\nThat take our breath away",
            "In your arms I find my sanctuary\nIn your heart I find my peace\nLove is our masterpiece",
            "Two hearts, one rhythm\nTwo souls, one dream\nLove is what we make it seem",
            "Your love is like morning dew\nFresh and pure, renewing my spirit\nEach day with you is a gift",
            "Love is not about finding someone to live with\nIt's about finding someone you can't live without",
            "In the silence between us\nI hear love speak\nIn words that need no sound",
            "Your presence is my greatest treasure\nYour love is my greatest wealth\nTogether we are complete",
            "Love is not blind\nIt sees but doesn't mind\nThe imperfections that make us human",
            "In your eyes I see my future\nIn your heart I find my past\nLove is what makes us last",
            "Love is the bridge between two hearts\nBuilt with trust and understanding\nStrong enough to last forever",
            "Your love fills the empty spaces\nIn my heart with grace\nYou are my saving place",
            "Love is not about perfection\nIt's about connection\nTwo souls in deep reflection",
            "In your embrace I find my strength\nIn your love I find my peace\nTogether all worries cease",
            "Love is the music of the heart\nPlaying in perfect harmony\nCreating our own symphony",
            "Your love is my anchor\nIn the stormy sea of life\nKeeping me steady and safe",
            "Love is not about possession\nIt's about expression\nOf deep affection",
            "In your presence I am complete\nIn your absence I am incomplete\nLove is what makes us meet",
            "Love is the language of the soul\nSpoken in whispers and glances\nUnderstood without words",
            "Your love is my greatest treasure\nMore valuable than gold\nA story forever told",
            "Love is not about changing someone\nIt's about accepting them\nJust as they are, perfectly imperfect"
        )
    }

    private fun getNaturePoems(): Array<String> {
        return arrayOf(
            "The morning sun rises\nKissing the earth with golden light\nNature awakens in delight",
            "In the forest's embrace\nWhere trees whisper ancient secrets\nI find my peace",
            "The ocean waves crash\nAgainst the shore with rhythm\nNature's eternal song",
            "Birds sing at dawn\nAnnouncing a new day\nNature's symphony begins",
            "The mountains stand tall\nWitnessing time's passage\nSilent and strong",
            "Raindrops fall gently\nNourishing the earth below\nLife begins to grow",
            "The sunset paints the sky\nIn hues of orange and gold\nNature's beauty unfolds",
            "In the garden's bloom\nFlowers dance in the breeze\nNature's poetry",
            "The river flows endlessly\nCarving paths through the land\nNature's gentle hand",
            "Stars twinkle in the night\nA canopy of light\nNature's wonder bright",
            "The wind whispers through leaves\nTelling stories of old\nNature's tales untold",
            "Butterflies dance in the air\nGraceful and free\nNature's beauty to see",
            "The moon reflects on water\nCreating silver paths\nNature's magic casts",
            "Seasons change with grace\nEach one has its place\nNature's endless race",
            "The forest floor is soft\nWith fallen leaves and moss\nNature's gentle loss",
            "Clouds drift across the sky\nPainting pictures up high\nNature's art to spy",
            "The waterfall cascades\nCreating mist and rainbows\nNature's wonder shows",
            "In the meadow green\nWildflowers sway unseen\nNature's gentle scene",
            "The desert blooms at night\nWhen the moon is bright\nNature's secret light",
            "The tree stands alone\nRooted deep in earth\nNature's proof of worth",
            "Snowflakes fall gently\nCovering the world in white\nNature's pure delight",
            "The ocean's depths hold secrets\nMysteries untold\nNature's stories bold",
            "The morning mist rises\nRevealing beauty below\nNature's gentle show",
            "The butterfly emerges\nFrom its cocoon of hope\nNature's miracle scope",
            "The mountains echo calls\nOf creatures great and small\nNature's voices all",
            "The river finds its way\nThrough obstacles and stones\nNature's persistence shown",
            "The garden grows with care\nNurtured by sun and rain\nNature's endless gain",
            "The sunset fades to night\nStars begin to shine\nNature's design",
            "The forest breathes with life\nIn every leaf and branch\nNature's endless dance",
            "In nature I find peace\nIn its beauty I find grace\nNature's loving embrace"
        )
    }

    private fun getMotivationPoems(): Array<String> {
        return arrayOf(
            "Rise up, stand tall\nYour dreams are worth it all\nBelieve and never fall",
            "Every step forward counts\nNo matter how small\nProgress is progress after all",
            "The journey of a thousand miles\nBegins with a single step\nTake that step today",
            "You are stronger than you know\nBraver than you believe\nWorthy of all you achieve",
            "Challenges are opportunities\nDisguised as obstacles\nEmbrace them and grow",
            "Your potential is limitless\nYour dreams are valid\nYour success is inevitable",
            "Don't wait for the perfect moment\nStart where you are\nUse what you have",
            "The only failure is giving up\nEvery attempt is progress\nKeep moving forward",
            "You have the power within\nTo change your story\nTo create your glory",
            "Success is not final\nFailure is not fatal\nIt's courage that counts",
            "Dream big, work hard\nStay focused and true\nSuccess will come to you",
            "The past doesn't define you\nThe future awaits\nToday you create",
            "Obstacles are stepping stones\nLeading to your dreams\nKeep climbing, it seems",
            "You are capable of amazing things\nBelieve in yourself\nYour potential brings",
            "Progress over perfection\nEvery step matters\nYour journey shatters",
            "The only way to fail\nIs to stop trying\nKeep flying",
            "Your journey is unique\nYour pace is perfect\nKeep moving, don't stop",
            "Challenges make you stronger\nSetbacks make you wiser\nKeep going higher",
            "The future belongs to those\nWho believe in their dreams\nAnd work for their schemes",
            "You are not defined\nBy your mistakes\nBut by how you rise",
            "Success is a journey\nNot a destination\nCelebrate each station",
            "Every expert was once a beginner\nEvery pro was once an amateur\nKeep learning, keep growing",
            "The only limits that exist\nAre the ones in your mind\nLeave them behind",
            "Your story is still being written\nMake it one worth reading\nKeep succeeding",
            "Dreams don't work\nUnless you do\nMake it happen, it's true",
            "The path to success\nIs paved with effort\nKeep going, don't desert",
            "You have within you\nEverything you need\nTo succeed",
            "The best time to start\nWas yesterday\nThe second best is today",
            "Your potential is endless\nYour dreams are valid\nYour success is real",
            "Keep going, keep growing\nKeep believing, keep achieving\nYour dreams are worth receiving"
        )
    }

    private fun getLifePoems(): Array<String> {
        return arrayOf(
            "Life is a journey\nNot a destination\nEnjoy the process",
            "In every moment\nThere is beauty\nIn every challenge, opportunity",
            "Life teaches us lessons\nThrough joy and through pain\nEach one helps us gain",
            "The present moment is a gift\nThat's why it's called present\nCherish it, be present",
            "Life is like a book\nEach day is a new page\nWrite your story with courage",
            "In the ups and downs\nWe find our strength\nWe find ourselves at length",
            "Life is not about waiting\nFor the storm to pass\nIt's about learning to dance in the rain",
            "Every ending is a beginning\nEvery loss is a lesson\nLife is a progression",
            "The beauty of life\nLies in its impermanence\nCherish each moment",
            "Life is not about perfection\nIt's about connection\nAnd deep reflection",
            "In the ordinary moments\nWe find extraordinary meaning\nLife is worth seeing",
            "Life is a canvas\nYou are the artist\nPaint it with passion",
            "The journey of life\nIs filled with surprises\nEmbrace the unexpected",
            "Life is not about having\nIt's about being\nAnd truly seeing",
            "In every breath\nThere is possibility\nIn every moment, opportunity",
            "Life is a dance\nBetween holding on and letting go\nLearn the rhythm, grow",
            "The secret of life\nIs to enjoy the passage of time\nMake it sublime",
            "Life is not about finding yourself\nIt's about creating yourself\nBecoming yourself",
            "In the simple moments\nWe find life's greatest joys\nListen to the noise",
            "Life is a story\nYou are the author\nWrite it with honor",
            "The beauty of life\nIs in its diversity\nEmbrace the variety",
            "Life teaches us\nTo appreciate what we have\nWhile we have it",
            "In every sunset\nThere is a promise of dawn\nLife goes on",
            "Life is not about the destination\nIt's about the journey\nMake it a good one",
            "The moments we remember\nAre not the ones we planned\nBut the ones that were grand",
            "Life is a gift\nUnwrap it carefully\nEnjoy it gratefully",
            "In the complexity of life\nWe find simplicity\nIn the mystery, clarity",
            "Life is not about waiting\nIt's about creating\nAnd celebrating",
            "The art of living\nIs finding joy in simple things\nThat life brings",
            "Life is a beautiful mess\nEmbrace it, don't stress\nJust do your best"
        )
    }

    private fun getMelancholyPoems(): Array<String> {
        return arrayOf(
            "In the quiet of the night\nI find my thoughts\nDrifting through memories",
            "The rain falls gently\nMirroring my tears\nWashing away my fears",
            "Loneliness is a friend\nWho visits uninvited\nStaying longer than needed",
            "In the silence\nI hear my heart\nBreaking apart",
            "The past haunts me\nLike a shadow\nFollowing wherever I go",
            "Tears fall like autumn leaves\nEach one a memory\nOf what used to be",
            "In the darkness\nI find my truth\nIn the sadness, my youth",
            "The empty spaces\nBetween my thoughts\nAre where I get lost",
            "Melancholy is a gentle sadness\nThat wraps around the heart\nLike a work of art",
            "In the quiet moments\nI feel the weight\nOf all I've lost",
            "The music plays\nBut I don't hear\nLost in my thoughts, I fear",
            "Nostalgia is a bittersweet feeling\nOf times gone by\nThat make me cry",
            "In the twilight hours\nI find my solitude\nIn the silence, my mood",
            "The fog of sadness\nEnvelops my soul\nTaking its toll",
            "Memories fade\nBut the pain remains\nLike autumn rains",
            "In the depth of melancholy\nI find beauty\nIn the sadness, truth",
            "The heart aches\nFor what was\nAnd what will never be",
            "Melancholy is not depression\nIt's a gentle sadness\nA quiet madness",
            "In the rain\nI find my tears\nWashing away my fears",
            "The past is a ghost\nThat haunts my present\nMaking me resent",
            "Sadness is a color\nThat paints my world\nIn shades of gray",
            "In the quiet\nI find my thoughts\nDrifting through time",
            "The melancholy moon\nShines on my sadness\nIlluminating my madness",
            "Tears are words\nThat the heart can't express\nIn its sadness",
            "In the silence\nI hear my heart\nBeating in the dark",
            "The weight of sadness\nIs heavy on my soul\nTaking its toll",
            "Melancholy is a friend\nWho understands\nThe weight of my hands",
            "In the depth of feeling\nI find my truth\nIn the sadness, my youth",
            "The past echoes\nIn my present\nMaking me resent",
            "In melancholy\nI find beauty\nIn sadness, truth"
        )
    }

    private fun getFriendshipPoems(): Array<String> {
        return arrayOf(
            "A true friend is a treasure\nRare and precious\nWorth more than gold",
            "Friendship is not about\nBeing together always\nBut being there when needed",
            "In your friendship\nI find my strength\nAnd my peace at length",
            "A friend is someone\nWho knows your story\nAnd loves you anyway",
            "Friendship is the bridge\nBetween two hearts\nThat never departs",
            "In laughter and in tears\nA friend is always near\nDispelling all fears",
            "True friendship is rare\nLike a precious gem\nCherish it, my friend",
            "A friend is someone\nWho sees the potential\nIn your imperfection",
            "Friendship is not about\nHaving many friends\nBut having true ones",
            "In your company\nI find my joy\nAnd my peace, my friend",
            "A friend is someone\nWho walks in when others\nWalk out",
            "Friendship is the music\nThat makes life beautiful\nAnd meaningful",
            "In your friendship\nI find my home\nAnd my peace, my friend",
            "A true friend is someone\nWho understands your silence\nAnd speaks your language",
            "Friendship is not about\nAgreeing on everything\nBut respecting differences",
            "In your friendship\nI find my strength\nAnd my courage at length",
            "A friend is someone\nWho celebrates your success\nAnd comforts your failure",
            "Friendship is the light\nThat guides us through darkness\nAnd brings us together",
            "In your company\nI find my peace\nAnd my joy, my friend",
            "A friend is someone\nWho knows your flaws\nAnd loves you still",
            "Friendship is not about\nBeing perfect\nBut being real",
            "In your friendship\nI find my truth\nAnd my peace, my friend",
            "A true friend is someone\nWho sees the best in you\nEven when you can't",
            "Friendship is the bond\nThat ties us together\nForever and ever",
            "In your company\nI find my home\nAnd my peace, my friend",
            "A friend is someone\nWho makes you laugh\nEven when you're sad",
            "Friendship is the gift\nThat keeps on giving\nAnd makes life worth living",
            "In your friendship\nI find my strength\nAnd my peace at length",
            "A friend is someone\nWho stands by you\nThrough thick and thin",
            "Friendship is the thread\nThat weaves our lives together\nAnd makes us stronger"
        )
    }

    private fun getSpiritualPoems(): Array<String> {
        return arrayOf(
            "In the silence of meditation\nI find my connection\nTo something greater",
            "The soul is eternal\nBeyond the physical\nConnected to the divine",
            "In prayer I find peace\nIn faith I find strength\nIn love I find truth",
            "The divine is within\nIn every breath\nIn every step",
            "Spirituality is the journey\nOf finding the divine\nWithin and without",
            "In the sacred moments\nI feel the presence\nOf something greater",
            "The soul seeks truth\nBeyond the physical realm\nIn the spiritual realm",
            "In connection with the divine\nI find my purpose\nAnd my peace",
            "Spirituality is not religion\nIt's a way of being\nAnd truly seeing",
            "The divine speaks\nIn whispers and signs\nIn the heart's designs",
            "In the depth of meditation\nI find my center\nAnd my peace",
            "The soul is a flame\nThat burns eternal\nIn the divine",
            "In prayer I connect\nWith something greater\nWith the creator",
            "Spirituality is the path\nOf inner discovery\nAnd outer recovery",
            "The divine is everywhere\nIn every moment\nIn every breath",
            "In the silence\nI hear the voice\nOf the divine",
            "The soul yearns\nFor connection\nWith the divine",
            "In spirituality\nI find my truth\nAnd my youth",
            "The divine is love\nIn its purest form\nIn every storm",
            "In meditation\nI find my peace\nAnd my release",
            "The soul is eternal\nBeyond time and space\nIn the divine's embrace",
            "In prayer I find\nMy connection\nTo the divine",
            "Spirituality is the journey\nOf becoming\nWho we truly are",
            "The divine is within\nWaiting to be discovered\nIn the heart uncovered",
            "In the sacred\nI find my truth\nAnd my youth",
            "The soul seeks\nUnity with the divine\nIn every line",
            "In spirituality\nI find my path\nAnd my peace at last",
            "The divine speaks\nIn the language of love\nFrom above",
            "In connection\nI find my purpose\nAnd my peace",
            "The soul is a bridge\nBetween the physical\nAnd the spiritual"
        )
    }

    private fun getHopePoems(): Array<String> {
        return arrayOf(
            "Hope is the light\nThat guides us through darkness\nAnd brings us to dawn",
            "In every sunset\nThere is a promise\nOf a new dawn",
            "Hope is the seed\nThat grows in darkness\nInto light",
            "Even in the darkest night\nThe stars still shine\nBringing hope divine",
            "Hope is not about\nIgnoring the darkness\nBut seeing the light",
            "In hope I find\nMy strength to carry on\nUntil the dawn",
            "Hope is the anchor\nThat keeps us steady\nIn the storm",
            "Even when all seems lost\nHope remains\nIn the heart",
            "Hope is the bridge\nBetween despair and joy\nBetween today and tomorrow",
            "In hope I find\nMy courage to continue\nAnd my faith to renew",
            "Hope is not wishful thinking\nIt's active belief\nIn possibilities",
            "The dawn always comes\nAfter the darkest night\nBringing light",
            "Hope is the flame\nThat never dies\nIn the heart",
            "In hope I find\nMy strength to rise\nAnd my will to try",
            "Hope is the melody\nThat plays in the heart\nEven in the dark",
            "Even in the storm\nHope is the rainbow\nThat promises tomorrow",
            "Hope is not about\nHaving all the answers\nBut believing in possibilities",
            "In hope I find\nMy peace\nAnd my release",
            "Hope is the light\nThat shines in darkness\nBringing brightness",
            "Even when the path is unclear\nHope lights the way\nTo a brighter day",
            "Hope is the song\nThat the heart sings\nIn the darkest times",
            "In hope I find\nMy strength\nAnd my length",
            "Hope is the promise\nThat tomorrow\nWill be better",
            "Even in the depths\nOf despair\nHope is there",
            "Hope is the bridge\nThat connects\nToday and tomorrow",
            "In hope I find\nMy courage\nAnd my message",
            "Hope is the light\nAt the end of the tunnel\nThat guides us through",
            "Even when all seems lost\nHope remains\nIn the heart's domain",
            "Hope is the seed\nOf possibility\nThat grows in reality",
            "In hope I find\nMy strength to carry on\nUntil the dawn"
        )
    }

    private fun getReflectionPoems(): Array<String> {
        return arrayOf(
            "In the mirror of reflection\nI see my true self\nBeyond the surface",
            "Self-reflection is the key\nTo understanding\nWho we truly are",
            "In quiet contemplation\nI find my answers\nIn my questions",
            "Reflection is not about\nLooking back with regret\nBut with understanding",
            "In the depth of reflection\nI find my truth\nAnd my youth",
            "Self-reflection is the journey\nOf inner discovery\nAnd outer recovery",
            "In reflection I see\nMy patterns and my growth\nMy truth and my worth",
            "Reflection is the mirror\nThat shows us\nWho we are",
            "In contemplation\nI find my peace\nAnd my release",
            "Self-reflection is not\nAbout judging ourselves\nBut understanding ourselves",
            "In the silence of reflection\nI hear my inner voice\nAnd my choice",
            "Reflection is the process\nOf becoming aware\nOf who we are",
            "In reflection I find\nMy lessons learned\nAnd my wisdom earned",
            "Self-reflection is the light\nThat illuminates\nOur inner world",
            "In contemplation\nI find my clarity\nAnd my reality",
            "Reflection is the bridge\nBetween who we were\nAnd who we are",
            "In the depth of reflection\nI find my growth\nAnd my truth",
            "Self-reflection is not\nAbout being perfect\nBut being aware",
            "In reflection I see\nMy patterns\nAnd my growth",
            "Reflection is the journey\nOf self-discovery\nAnd recovery",
            "In contemplation\nI find my answers\nIn my questions",
            "Self-reflection is the key\nTo personal growth\nAnd self-knowledge",
            "In reflection I find\nMy truth\nAnd my youth",
            "Reflection is the mirror\nThat reflects\nOur inner world",
            "In the silence\nI find my answers\nIn my questions",
            "Self-reflection is the path\nTo understanding\nWho we are",
            "In reflection I see\nMy growth\nAnd my worth",
            "Reflection is the light\nThat shows us\nOur inner world",
            "In contemplation\nI find my peace\nAnd my release",
            "Self-reflection is the journey\nOf becoming\nWho we are meant to be"
        )
    }

    private fun getCouragePoems(): Array<String> {
        return arrayOf(
            "Courage is not the absence of fear\nBut the decision to act\nDespite the fear",
            "In courage I find\nMy strength to face\nWhat I fear",
            "Courage is the bridge\nBetween fear and freedom\nBetween doubt and action",
            "Bravery is not about\nBeing fearless\nBut being brave despite fear",
            "In courage I find\nMy power to overcome\nAnd my strength to become",
            "Courage is the light\nThat shines in darkness\nGuiding us through",
            "Even when afraid\nCourage helps us\nTake the next step",
            "In bravery I find\nMy strength to stand\nAnd my will to withstand",
            "Courage is not about\nNever falling\nBut always rising",
            "In courage I find\nMy truth\nAnd my youth",
            "Bravery is the choice\nTo face our fears\nAnd overcome them",
            "Courage is the flame\nThat burns in the heart\nEven in the dark",
            "In courage I find\nMy strength to continue\nAnd my will to renew",
            "Courage is not about\nHaving no fear\nBut acting despite fear",
            "In bravery I find\nMy power\nAnd my hour",
            "Courage is the bridge\nThat connects\nFear and freedom",
            "Even when trembling\nCourage helps us\nTake the step",
            "In courage I find\nMy strength to rise\nAnd my will to try",
            "Courage is the light\nAt the end of the tunnel\nThat guides us through",
            "Bravery is not about\nNever being afraid\nBut always being brave",
            "In courage I find\nMy truth\nAnd my youth",
            "Courage is the seed\nThat grows in adversity\nInto strength",
            "Even in the storm\nCourage helps us\nStand strong",
            "In bravery I find\nMy strength to carry on\nUntil the dawn",
            "Courage is the melody\nThat plays in the heart\nEven in the dark",
            "In courage I find\nMy power to overcome\nAnd my strength to become",
            "Courage is not about\nNever falling\nBut always rising",
            "In bravery I find\nMy truth\nAnd my youth",
            "Courage is the flame\nThat never dies\nIn the heart",
            "Even when afraid\nCourage helps us\nTake the leap"
        )
    }

    private fun getWisdomPoems(): Array<String> {
        return arrayOf(
            "Wisdom is not about\nKnowing everything\nBut understanding what matters",
            "In wisdom I find\nMy peace\nAnd my release",
            "Wisdom is the light\nThat illuminates\nThe path of life",
            "True wisdom comes\nFrom experience\nAnd reflection",
            "In wisdom I find\nMy truth\nAnd my youth",
            "Wisdom is not about\nHaving all the answers\nBut asking the right questions",
            "In the depth of wisdom\nI find my understanding\nAnd my peace",
            "Wisdom is the bridge\nBetween knowledge and understanding\nBetween learning and being",
            "True wisdom is\nKnowing what you don't know\nAnd being okay with it",
            "In wisdom I find\nMy clarity\nAnd my reality",
            "Wisdom is the gift\nThat comes with age\nAnd experience",
            "In the silence of wisdom\nI hear my inner voice\nAnd my choice",
            "Wisdom is not about\nBeing right\nBut being wise",
            "In wisdom I find\nMy peace\nAnd my release",
            "Wisdom is the light\nThat guides us\nThrough life",
            "True wisdom is\nKnowing when to speak\nAnd when to listen",
            "In wisdom I find\nMy understanding\nAnd my peace",
            "Wisdom is the journey\nOf learning\nAnd growing",
            "In the depth of wisdom\nI find my truth\nAnd my youth",
            "Wisdom is not about\nHaving all the answers\nBut understanding the questions",
            "In wisdom I find\nMy clarity\nAnd my reality",
            "Wisdom is the bridge\nBetween knowledge and understanding\nBetween learning and being",
            "True wisdom comes\nFrom experience\nAnd reflection",
            "In wisdom I find\nMy peace\nAnd my release",
            "Wisdom is the light\nThat illuminates\nThe path of life",
            "In the silence of wisdom\nI hear my inner voice\nAnd my choice",
            "Wisdom is not about\nBeing perfect\nBut being wise",
            "In wisdom I find\nMy truth\nAnd my youth",
            "Wisdom is the gift\nThat comes with understanding\nAnd experience",
            "In wisdom I find\nMy peace\nAnd my release"
        )
    }

    private fun getPeacePoems(): Array<String> {
        return arrayOf(
            "Peace is not the absence of chaos\nBut the presence of calm\nIn the heart",
            "In peace I find\nMy center\nAnd my truth",
            "Peace is the silence\nThat speaks louder\nThan words",
            "True peace comes\nFrom within\nNot from without",
            "In peace I find\nMy refuge\nAnd my strength",
            "Peace is the bridge\nBetween chaos and calm\nBetween noise and silence",
            "In the stillness of peace\nI find my answers\nAnd my truth",
            "Peace is not about\nAvoiding conflict\nBut finding harmony",
            "In peace I find\nMy home\nAnd my truth",
            "Peace is the light\nThat shines in darkness\nBringing calm",
            "True peace is\nBeing at ease\nWith yourself",
            "In peace I find\nMy strength\nAnd my length",
            "Peace is the melody\nThat plays in the heart\nBringing calm",
            "In the depth of peace\nI find my center\nAnd my truth",
            "Peace is not about\nHaving no problems\nBut having inner calm",
            "In peace I find\nMy refuge\nAnd my strength",
            "Peace is the anchor\nThat keeps us steady\nIn the storm",
            "True peace comes\nFrom acceptance\nAnd letting go",
            "In peace I find\nMy home\nAnd my truth",
            "Peace is the bridge\nBetween chaos and calm\nBetween noise and silence",
            "In the stillness of peace\nI find my answers\nAnd my truth",
            "Peace is not about\nBeing perfect\nBut being at peace",
            "In peace I find\nMy strength\nAnd my length",
            "Peace is the light\nThat guides us\nThrough life",
            "True peace is\nFinding calm\nIn the chaos",
            "In peace I find\nMy refuge\nAnd my strength",
            "Peace is the melody\nThat plays in the heart\nBringing calm",
            "In the depth of peace\nI find my center\nAnd my truth",
            "Peace is not about\nHaving no problems\nBut having inner calm",
            "In peace I find\nMy home\nAnd my truth"
        )
    }

    private fun getDreamsPoems(): Array<String> {
        return arrayOf(
            "Dreams are the seeds\nOf possibility\nThat grow in reality",
            "In dreams I find\nMy inspiration\nAnd my destination",
            "Dreams are the bridge\nBetween imagination and reality\nBetween hope and possibility",
            "True dreams are\nNot just wishes\nBut visions with action",
            "In dreams I find\nMy purpose\nAnd my worth",
            "Dreams are the light\nThat guides us\nThrough darkness",
            "Even when dreams seem far\nThey light the way\nTo a brighter day",
            "In dreams I find\nMy strength to carry on\nUntil the dawn",
            "Dreams are not about\nEscaping reality\nBut creating it",
            "In dreams I find\nMy truth\nAnd my youth",
            "Dreams are the melody\nThat plays in the heart\nEven in the dark",
            "True dreams are\nThose that inspire action\nAnd create passion",
            "In dreams I find\nMy inspiration\nAnd my destination",
            "Dreams are the bridge\nBetween who we are\nAnd who we can be",
            "Even when dreams seem impossible\nThey light the way\nTo possibility",
            "In dreams I find\nMy strength\nAnd my length",
            "Dreams are not about\nHaving all the answers\nBut having the vision",
            "In dreams I find\nMy purpose\nAnd my worth",
            "Dreams are the light\nAt the end of the tunnel\nThat guides us through",
            "True dreams are\nThose that push us\nTo become better",
            "In dreams I find\nMy inspiration\nAnd my destination",
            "Dreams are the seed\nThat grows in the heart\nInto reality",
            "Even when dreams seem far\nThey light the way\nTo a brighter day",
            "In dreams I find\nMy strength to carry on\nUntil the dawn",
            "Dreams are not about\nEscaping reality\nBut creating it",
            "In dreams I find\nMy truth\nAnd my youth",
            "Dreams are the melody\nThat plays in the heart\nEven in the dark",
            "True dreams are\nThose that inspire action\nAnd create passion",
            "In dreams I find\nMy inspiration\nAnd my destination",
            "Dreams are the bridge\nBetween imagination and reality\nBetween hope and possibility"
        )
    }

    private fun getTimePoems(): Array<String> {
        return arrayOf(
            "Time is a river\nThat flows endlessly\nCarrying us forward",
            "In time I find\nMy past\nAnd my future",
            "Time is the bridge\nBetween yesterday and tomorrow\nBetween memory and hope",
            "True time is\nNot about quantity\nBut quality",
            "In time I find\nMy growth\nAnd my truth",
            "Time is the light\nThat illuminates\nThe path of life",
            "Even as time passes\nMemories remain\nIn the heart",
            "In time I find\nMy strength to carry on\nUntil the dawn",
            "Time is not about\nHaving more\nBut making it count",
            "In time I find\nMy truth\nAnd my youth",
            "Time is the melody\nThat plays in the heart\nMarking each moment",
            "True time is\nSpent in presence\nNot in absence",
            "In time I find\nMy growth\nAnd my truth",
            "Time is the bridge\nBetween who we were\nAnd who we are",
            "Even as time flies\nMoments remain\nIn the heart",
            "In time I find\nMy strength\nAnd my length",
            "Time is not about\nCounting the days\nBut making days count",
            "In time I find\nMy growth\nAnd my truth",
            "Time is the light\nThat shows us\nOur journey",
            "True time is\nBeing present\nIn the moment",
            "In time I find\nMy past\nAnd my future",
            "Time is the river\nThat carries us\nForward",
            "Even as time passes\nMemories remain\nIn the heart",
            "In time I find\nMy strength to carry on\nUntil the dawn",
            "Time is not about\nHaving more\nBut making it count",
            "In time I find\nMy truth\nAnd my youth",
            "Time is the melody\nThat plays in the heart\nMarking each moment",
            "True time is\nSpent in presence\nNot in absence",
            "In time I find\nMy growth\nAnd my truth",
            "Time is the bridge\nBetween yesterday and tomorrow\nBetween memory and hope"
        )
    }

    private fun getBeautyPoems(): Array<String> {
        return arrayOf(
            "Beauty is not in the face\nBut in the heart\nAnd in the soul",
            "In beauty I find\nMy inspiration\nAnd my peace",
            "Beauty is the light\nThat shines from within\nIlluminating the world",
            "True beauty is\nNot about perfection\nBut about authenticity",
            "In beauty I find\nMy truth\nAnd my youth",
            "Beauty is the melody\nThat plays in the heart\nBringing joy",
            "Even in the ordinary\nBeauty can be found\nIn the heart",
            "In beauty I find\nMy strength\nAnd my length",
            "Beauty is not about\nLooking perfect\nBut feeling perfect",
            "In beauty I find\nMy truth\nAnd my youth",
            "Beauty is the light\nThat shines from within\nIlluminating the world",
            "True beauty is\nIn the simplicity\nOf being yourself",
            "In beauty I find\nMy inspiration\nAnd my peace",
            "Beauty is the bridge\nBetween the physical\nAnd the spiritual",
            "Even in the simple\nBeauty can be found\nIn the heart",
            "In beauty I find\nMy strength\nAnd my length",
            "Beauty is not about\nHaving it all\nBut appreciating what you have",
            "In beauty I find\nMy truth\nAnd my youth",
            "Beauty is the melody\nThat plays in the heart\nBringing joy",
            "True beauty is\nIn the authenticity\nOf being yourself",
            "In beauty I find\nMy inspiration\nAnd my peace",
            "Beauty is the light\nThat shines from within\nIlluminating the world",
            "Even in the ordinary\nBeauty can be found\nIn the heart",
            "In beauty I find\nMy strength\nAnd my length",
            "Beauty is not about\nLooking perfect\nBut feeling perfect",
            "In beauty I find\nMy truth\nAnd my youth",
            "Beauty is the melody\nThat plays in the heart\nBringing joy",
            "True beauty is\nIn the simplicity\nOf being yourself",
            "In beauty I find\nMy inspiration\nAnd my peace",
            "Beauty is the light\nThat shines from within\nIlluminating the world"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            // Add new categories and "User Added" category
            seedCategories(db)
        }
        if (oldVersion < 5) {
            // Add default poems for all categories
            seedPoems(db)
        }
    }
}