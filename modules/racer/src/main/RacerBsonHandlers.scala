package lila.racer

import chess.format.FEN
import chess.format.Uci
import reactivemongo.api.bson.*

import lila.db.dsl.{ *, given }
import lila.puzzle.BsonHandlers.given
import lila.puzzle.Puzzle
import lila.storm.StormPuzzle

private object RacerBsonHandlers:

  given BSONDocumentReader[StormPuzzle] with
    def readDocument(r: BSONDocument) = for {
      id      <- r.getAsTry[PuzzleId]("_id")
      fen     <- r.getAsTry[FEN]("fen")
      lineStr <- r.getAsTry[String]("line")
      line    <- lineStr.split(' ').toList.flatMap(Uci.Move.apply).toNel.toTry("Empty move list?!")
      glicko  <- r.getAsTry[Bdoc]("glicko")
      rating  <- glicko.getAsTry[Double]("r")
    } yield StormPuzzle(id, fen, line, rating.toInt)
