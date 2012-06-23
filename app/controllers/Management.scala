package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.{Logger}
import play.api.i18n.Messages
import play.api.mvc.{Controller}
import models._
import java.util

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Controller for Management operations
 */
object Management extends Controller with AccessControl {

  //form to store quotes
  val quotesForm: Form[QuoteForm] = Form(
    mapping(
      "quote" -> list(nonEmptyText)
    )(QuoteForm.apply)(QuoteForm.unapply)
  )


  /**
   * Displays the edit page for quotes
   */
  def editQuotes() = isDev {
    implicit request =>
      Logger.info("Management.editQuotes accessed")
      val quotes = QuoteForm(Quote.all().map{ q => q.quote})
      Ok(views.html.management.editQuotes(quotesForm.fill(quotes)))
  }

  /**
   * Saves the list of quotes
   */
  def saveQuotes() = isDev {
    implicit request =>
      Logger.info("Management.saveQuotes accessed")
      quotesForm.bindFromRequest.fold(
        // Form has errors, redisplay it
        errors => {
          Logger.error("Management.saveQuotes errors while obtaining Quotes: %s".format(errors))
          BadRequest(views.html.management.editQuotes(errors))
        },
        // We got a valid value, update
        quotes => {
          Logger.info("Management.saveQuotes saving quotes[%s]".format(quotes))
          Quote.save(quotes.list.map { q => Quote(q) })
          Redirect(routes.Management.editQuotes()).flashing("success" -> Messages("quotes.edit.saved"))
        }
      )
  }

  // form to store posts
  val postForm: Form[PostText] = Form(
    mapping(
      "title" -> nonEmptyText,
      "date" -> optional(date),
      "content" -> nonEmptyText,
      "tags" -> nonEmptyText
    )(PostText.apply)(PostText.unapply)
  )

  /**
   * Allows user to add a new Post
   */
  def addPost() = isDev {
    implicit request =>
      Logger.info("Management.addPost accessed")
      Ok(views.html.management.addPost(postForm))
  }

  /**
   * Saves the contents of the edited post
   */
  def savePost() = isDev {
    implicit request =>
      Logger.info("Management.savePost accessed")
      postForm.bindFromRequest.fold(
        // Form has errors, redisplay it
        errors => {
          Logger.error("Management.savePost errors while obtaining Post: %s".format(errors))
          BadRequest(views.html.management.addPost(errors))
        },
        // We got a valid value, update
        post => {
          Logger.info("Management.savePost saving Post[%s]".format(post))
          Post.save(post)
          Redirect(routes.Management.listPosts()).flashing("success" -> Messages("posts.edit.saved"))
        }
      )
  }

  /**
   * Shows all the posts in the system ordered by date of publication.
   * As this will only be used in localhost (dev mode) there is no pagination
   */
  def listPosts() = isDev {
    implicit request =>
      Logger.info("Management.listPosts accessed")
      val list = Post.all()
      Ok(views.html.management.listPosts(list))
  }

  /**
   * Deletes the post with the given id
   * @param id id of the post to delete
   */
  def deletePost(id: Int) = isDev {
    implicit request =>
      Logger.info("Management.deletePost accessed for post[%d]".format(id))
      Post.delete(id)
      Redirect(routes.Management.listPosts()).flashing("success" -> Messages("posts.delete.ok"))
  }

  /**
   * Edits the contents of the post with the given id
   * @param id id of the post to edit
   */
  def editPost(id: Int) = isDev {
    implicit request =>
      Logger.info("Management.editPost accessed for post[%d]".format(id))
      Post.getById(id) match {
        case Some(post) => {
          val postText = new PostText(title = post.title, date = None, content = Post.getContent(post), tags = post.tags.getOrElse(Array()).toList.mkString(","))
          Ok(views.html.management.editPost(id, postForm.fill(postText)))
        }
        case _ => {
          Redirect(routes.Management.listPosts()).flashing("error" -> Messages("posts.edit.notFound"))
        }
      }
  }

  /**
   * Saves changes to the post with given id
   * @param id id of the post to update
   */
  def updatePost(id: Int) = isDev {
    implicit request =>
      Logger.info("Management.updatePost accessed for post[%d]".format(id))
      postForm.bindFromRequest.fold(
        // Form has errors, redisplay it
        errors => {
          Logger.error("Management.updatePost errors while obtaining Post: %s".format(errors))
          BadRequest(views.html.management.editPost(id, errors))
        },
        // We got a valid value, update
        post => {
          Logger.info("Management.updatePost updating Post[%s]".format(post))
          Post.update(post, id)
          Redirect(routes.Management.listPosts()).flashing("success" -> Messages("posts.edit.saved"))
        }
      )
  }

  // form to store bio
  val bioForm: Form[Bio] = Form(
    mapping(
      "full" -> nonEmptyText,
      "short" -> nonEmptyText
    )(Bio.apply)(Bio.unapply)
  )

  /**
   * Allows user to edit the About info
   */
  def editBio() = isDev {
    implicit request =>
      Logger.info("Management.editBio accessed")
      val bio = new Bio(Bio.getFullBio(), Bio.getBio())
      Ok(views.html.management.editBio(bioForm.fill(bio)))
  }

  /**
   * Saves the contents of the edited bio
   */
  def saveBio() = isDev {
    implicit request =>
      Logger.info("Management.saveBio accessed")
      bioForm.bindFromRequest.fold(
        // Form has errors, redisplay it
        errors => {
          Logger.error("Management.saveBio errors while obtaining Bio: %s".format(errors))
          BadRequest(views.html.management.editBio(errors))
        },
        // We got a valid value, update
        bio => {
          Logger.info("Management.saveBio saving Bio[%s]".format(bio))
          Bio.save(bio)
          Redirect(routes.Application.index()).flashing("success" -> Messages("bio.edit.saved"))
        }
      )
  }

  // form to store project
  val projectForm: Form[Project] = Form(
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "image" -> nonEmptyText,
      "link" -> nonEmptyText,
      "added" -> date,
      "comment" -> nonEmptyText,
      "status" -> nonEmptyText
    )(Project.apply)(Project.unapply)
  )

  /**
   * Shows all the posts in the system ordered by date of publication.
   * As this will only be used in localhost (dev mode) there is no pagination
   */
  def listProjects() = isDev {
    implicit request =>
      Logger.info("Management.listProjects accessed")
      val list = Project.all()
      Ok(views.html.management.listProjects(list))
  }

  /**
   * Deletes the project with the given id
   * @param id id of the post to delete
   */
  def deleteProject(id: Int) = isDev {
    implicit request =>
      Logger.info("Management.deleteProject accessed for project[%d]".format(id))
      Project.delete(id)
      Redirect(routes.Management.listProjects()).flashing("success" -> Messages("projects.delete.ok"))
  }

  /**
   * Edits the contents of the project with the given id
   * @param id id of the project to edit
   */
  def editProject(id: Int) = isDev {
    implicit request =>
      Logger.info("Management.editProject accessed for project[%d]".format(id))
      Project.getById(id) match {
        case project: Project => {
          Ok(views.html.management.editProject(id, projectForm.fill(project)))
        }
        case _ => {
          Redirect(routes.Management.listProjects()).flashing("error" -> Messages("projects.edit.notFound"))
        }
      }
  }

  /**
   * Saves changes to the project with given id
   * @param id id of the project to update
   */
  def updateProject(id: Int) = isDev {
    implicit request =>
      Logger.info("Management.updateProject accessed for project[%d]".format(id))
      projectForm.bindFromRequest.fold(
        // Form has errors, redisplay it
        errors => {
          Logger.error("Management.updateProject errors while obtaining project: %s".format(errors))
          BadRequest(views.html.management.editProject(id, errors))
        },
        // We got a valid value, update
        project => {
          Logger.info("Management.updateProject updating project[%s]".format(project))
          Project.update(project)
          Redirect(routes.Management.listProjects()).flashing("success" -> Messages("projects.edit.saved"))
        }
      )
  }

  /**
   * Allows user to add a new project to the list
   */
  def addProject() = isDev {
    implicit request =>
      Logger.info("Management.addProject accessed")
      Ok(views.html.management.addProject(projectForm))
  }

  /**
   * Saves the contents of the edited project
   */
  def saveProject() = isDev {
    implicit request =>
      Logger.info("Management.saveProject accessed")
      projectForm.bindFromRequest.fold(
        // Form has errors, redisplay it
        errors => {
          Logger.error("Management.saveProject errors while obtaining project: %s".format(errors))
          BadRequest(views.html.management.addProject(errors))
        },
        // We got a valid value, update
        project => {
          Logger.info("Management.saveProject saving project[%s]".format(project))
          Project.save(project)
          Redirect(routes.Management.listProjects()).flashing("success" -> Messages("projects.edit.saved"))
        }
      )
  }

  // form to store Link
  val linkForm: Form[Link] = Form(
    mapping(
      "id" -> number,
      "link" -> nonEmptyText,
      "comment" -> optional(nonEmptyText),
      "archive" -> optional(boolean),
      "category" -> optional(nonEmptyText),
      "subcategory" -> optional(nonEmptyText),
      "subject" -> optional(nonEmptyText)
    ){
      (id, link, comment, archive, category, subcategory, subject) => Link(id, link, comment.getOrElse(""), archive.getOrElse(false), category.getOrElse("Miscellanea"), subcategory.getOrElse("Generic"), subject.getOrElse("Other"))
    }{
      link: Link => Some(link.id,link.link,Some(link.comment),Some(link.archive),Some(link.category),Some(link.subcategory),Some(link.subject))
    }
  )

  /**
   * Shows all the links in the system ordered by date of publication.
   * As this will only be used in localhost (dev mode) there is no pagination
   */
  def listLinks() = isDev {
    implicit request =>
      Logger.info("Management.listLinks accessed")
      val archived = Link.getAllArchived()
      val unchecked = Link.getAllUnchecked()
      Ok(views.html.management.listLinks(archived, unchecked, linkForm))
  }

  /**
   * Deletes the link with the given id
   * @param id id of the link to delete
   */
  def deleteLink(id: Int) = isDev {
    implicit request =>
      Logger.info("Management.deleteLink accessed for link[%d]".format(id))
      Link.delete(id)
      Redirect(routes.Management.listLinks()).flashing("success" -> Messages("links.delete.ok"))
  }

  /**
   * Edits the contents of the link with the given id
   * @param id id of the link to edit
   */
  def editLink(id: Int) = isDev {
    implicit request =>
      Logger.info("Management.editLink accessed for link[%d]".format(id))
      Link.getById(id) match {
        case link: Link => {
          Ok(views.html.management.editLink(id, linkForm.fill(link)))
        }
        case _ => {
          Redirect(routes.Management.listLinks()).flashing("error" -> Messages("links.edit.notFound"))
        }
      }
  }

  /**
   * Saves changes to the link with given id
   * @param id id of the link to update
   */
  def updateLink(id: Int) = isDev {
    implicit request =>
      Logger.info("Management.updateLink accessed for link[%d]".format(id))
      linkForm.bindFromRequest.fold(
        // Form has errors, redisplay it
        errors => {
          Logger.error("Management.updateLink errors while obtaining link: %s".format(errors))
          BadRequest(views.html.management.editLink(id, errors))
        },
        // We got a valid value, update
        link => {
          Logger.info("Management.updateLink updating link[%s]".format(link))
          Link.update(link)
          Redirect(routes.Management.listLinks()).flashing("success" -> Messages("links.edit.saved"))
        }
      )
  }

  /**
   * Saves the contents of the edited project
   */
  def saveLink() = isDev {
    implicit request =>
      Logger.info("Management.saveLink accessed")
      linkForm.bindFromRequest.fold(
        // Form has errors, redisplay it
        errors => {
          Logger.error("Management.saveLink errors while obtaining link: %s".format(errors))
          val archived = Link.getAllArchived()
          val unchecked = Link.getAllUnchecked()
          BadRequest(views.html.management.listLinks(archived, unchecked, errors))
        },
        // We got a valid value, update
        link => {
          Logger.info("Management.saveLink saving link[%s]".format(link))
          if(Link.save(link)) {
            Redirect(routes.Management.listLinks()).flashing("success" -> Messages("links.edit.saved"))
          } else {
            Redirect(routes.Management.listLinks()).flashing("warning" -> Messages("links.edit.existing"))
          }
        }
      )
  }

}
