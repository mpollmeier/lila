package lila.user

import play.api.libs.json._
import User.{ LightPerf, PlayTime }

import lila.common.Json.jodaWrites
import lila.rating.{ Perf, PerfType }

final class JsonView(isOnline: lila.socket.IsOnline) {

  import JsonView._
  implicit private val profileWrites  = Json.writes[Profile]
  implicit private val playTimeWrites = Json.writes[PlayTime]

  def full(u: User, onlyPerf: Option[PerfType] = None, withOnline: Boolean): JsObject =
    if (u.disabled) disabled(u)
    else
      Json
        .obj(
          "id"        -> u.id,
          "username"  -> u.username,
          "perfs"     -> perfs(u, onlyPerf),
          "createdAt" -> u.createdAt
        )
        .add("online" -> withOnline.option(isOnline(u.id)))
        .add("tosViolation" -> u.lame)
        .add("profile" -> u.profile.map(p => profileWrites.writes(p.filterTroll(u.marks.troll)).noNull))
        .add("seenAt" -> u.seenAt)
        .add("patron" -> u.isPatron)
        .add("playTime" -> u.playTime)
        .add("title" -> u.title)
        .add("verified" -> u.isVerified)

  def minimal(u: User, onlyPerf: Option[PerfType]) =
    if (u.disabled) disabled(u)
    else
      Json
        .obj(
          "id"       -> u.id,
          "username" -> u.username,
          "online"   -> isOnline(u.id),
          "perfs"    -> perfs(u, onlyPerf)
        )
        .add("title" -> u.title)
        .add("tosViolation" -> u.lame)
        .add("profile" -> u.profile.flatMap(_.country).map { country =>
          Json.obj("country" -> country)
        })
        .add("patron" -> u.isPatron)
        .add("verified" -> u.isVerified)

  def lightPerfIsOnline(lp: LightPerf) =
    lightPerfWrites.writes(lp).add("online" -> isOnline(lp.user.id))

  def disabled(u: User) = Json.obj(
    "id"       -> u.id,
    "username" -> u.username,
    "disabled" -> true
  )
}

object JsonView {

  import Title.titleJsonWrites

  implicit val nameWrites = Writes[User] { u =>
    JsString(u.username)
  }

  implicit val lightPerfWrites = OWrites[LightPerf] { l =>
    Json
      .obj(
        "id"       -> l.user.id,
        "username" -> l.user.name,
        "perfs" -> Json.obj(
          l.perfKey -> Json.obj("rating" -> l.rating, "progress" -> l.progress)
        )
      )
      .add("title" -> l.user.title)
      .add("patron" -> l.user.isPatron)
  }

  implicit val modWrites = OWrites[User] { u =>
    Json
      .obj(
        "id"       -> u.id,
        "username" -> u.username,
        "title"    -> u.title,
        "games"    -> u.count.game
      )
      .add("tos" -> u.marks.dirty)
      .add("title" -> u.title)
  }

  implicit val perfWrites: OWrites[Perf] = OWrites { o =>
    Json
      .obj(
        "games"  -> o.nb,
        "rating" -> o.glicko.rating.toInt,
        "rd"     -> o.glicko.deviation.toInt,
        "prog"   -> o.progress
      )
      .add("prov" -> o.glicko.provisional)
  }

  private val standardPerfKeys: Set[Perf.Key] = PerfType.standard.map(_.key).to(Set)

  private def select(key: String, perf: Perf) =
    perf.nb > 0 || standardPerfKeys(key)

  def perfs(u: User, onlyPerf: Option[PerfType] = None) =
    JsObject(u.perfs.perfsMap collect {
      case (key, perf) if onlyPerf.fold(select(key, perf))(_.key == key) =>
        key -> perfWrites.writes(perf)
    }).add(
      "storm",
      u.perfs.storm.nonEmpty option Json.obj(
        "runs"  -> u.perfs.storm.runs,
        "score" -> u.perfs.storm.score
      )
    ).add(
      "racer",
      u.perfs.racer.nonEmpty option Json.obj(
        "runs"  -> u.perfs.racer.runs,
        "score" -> u.perfs.racer.score
      )
    ).add(
      "streak",
      u.perfs.streak.nonEmpty option Json.obj(
        "runs"  -> u.perfs.streak.runs,
        "score" -> u.perfs.streak.score
      )
    )

  def perfs(u: User, onlyPerfs: List[PerfType]) =
    JsObject(onlyPerfs.map { perfType =>
      perfType.key -> perfWrites.writes(u.perfs(perfType))
    })
}
