package lila.racer

import org.joda.time.format.DateTimeFormat
import play.api.libs.json._

import lila.common.Json._
import lila.common.LightUser
import lila.storm.StormJson
import lila.storm.StormPuzzle
import lila.storm.StormSign
import lila.user.User

final class RacerJson(stormJson: StormJson, sign: StormSign, lightUserSync: LightUser.GetterSync) {

  import StormJson._

  implicit private val playerWrites = OWrites[RacerPlayer] { p =>
    val user = p.userId flatMap lightUserSync
    Json
      .obj("name" -> p.name, "moves" -> p.moves)
      .add("userId" -> p.userId)
      .add("title" -> user.map(_.title))
  }

  // full race data
  def data(race: RacerRace, player: RacerPlayer) =
    Json
      .obj(
        "race" -> Json
          .obj(
            "id"    -> race.id.value,
            "moves" -> race.moves
          )
          .add("alreadyStarted" -> race.hasStarted),
        "player"  -> player,
        "puzzles" -> race.puzzles
      ) ++ state(race)

  // socket updates
  def state(race: RacerRace) = Json
    .obj(
      "players" -> race.players
    )
    .add("startsIn" -> race.startsInMillis)
    .add("finished" -> race.finishedAt.isDefined)
}
