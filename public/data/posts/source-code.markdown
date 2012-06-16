This is code

    /**
     * Returns the bio stored in markdown documents
     * The bio is stored in 2 places:
     *   - /public/data/whoami.markdown   contains a short description
     *   - /public/data/fullbio.markdown  contains the full bio
     */
    object Bio {
    
      val shortBio = "public/data/whoami.markdown"
      val fullBio = "public/data/fullbio.markdown"
    
      /**
       * Initializes the cached structures for the application
       */
      def init() = {
        getBio()
        getFullBio()
      }
    
    
      def getBio() = {
        Logger.info("Bio.getBio - Loading Bio")
        Cache.getOrElse("shortBio", controllers.Application.cacheStorage){
          Logger.info("Bio.getBio - Bio data not in cache, loading from file")
          MarkdownSupport.loadMarkdown(shortBio)
        }
      }
    
      def getFullBio() = {
        Logger.info("Bio.getFullBio - Loading Bio")
        Cache.getOrElse("fullBio", controllers.Application.cacheStorage){
          Logger.info("Bio.getFullBio - Bio data not in cache, loading from file")
          MarkdownSupport.loadMarkdown(fullBio)
        }
      }
    
      /**
       * Saves into filesystem the given bio
       * @param bio the bio content to store
       */
      def save(bio: Bio) = {
        Logger.info("Bio.save - saving contents into files")
        MarkdownSupport.saveMarkdown(fullBio, bio.full)
        MarkdownSupport.saveMarkdown(shortBio, bio.short)
      }
    
    }

hehe