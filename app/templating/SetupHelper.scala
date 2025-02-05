package lila.app
package templating

import chess.{ Mode, Speed }
import chess.variant.Variant
import play.api.i18n.Lang

import lila.i18n.{ I18nKeys as trans }
import lila.pref.Pref
import lila.report.Reason
import lila.setup.TimeMode

trait SetupHelper { self: I18nHelper =>

  type SelectChoice = (String, String, Option[String])

  val clockTimeChoices: List[SelectChoice] = List(
    ("0", "0", none),
    ("0.25", "¼", none),
    ("0.5", "½", none),
    ("0.75", "¾", none)
  ) ::: List(
    "1",
    "1.5",
    "2",
    "3",
    "4",
    "5",
    "6",
    "7",
    "8",
    "9",
    "10",
    "11",
    "12",
    "13",
    "14",
    "15",
    "16",
    "17",
    "18",
    "19",
    "20",
    "25",
    "30",
    "35",
    "40",
    "45",
    "60",
    "75",
    "90",
    "105",
    "120",
    "135",
    "150",
    "165",
    "180"
  ).map { v =>
    (v, v, none)
  }

  val clockIncrementChoices: List[SelectChoice] = {
    (0 to 20).toList ::: List(25, 30, 35, 40, 45, 60, 90, 120, 150, 180)
  } map { s =>
    (s.toString, s.toString, none)
  }

  val corresDaysChoices: List[SelectChoice] =
    ("1", "One day", none) :: List(2, 3, 5, 7, 10, 14).map { d =>
      (d.toString, s"$d days", none)
    }

  def translatedTimeModeChoices(using lang: Lang) =
    List(
      (TimeMode.RealTime.id.toString, trans.realTime.txt(), none),
      (TimeMode.Correspondence.id.toString, trans.correspondence.txt(), none),
      (TimeMode.Unlimited.id.toString, trans.unlimited.txt(), none)
    )

  def translatedReasonChoices(using lang: Lang) =
    List(
      (Reason.Cheat.key, trans.cheat.txt()),
      (Reason.Comm.key, trans.insult.txt()),
      (Reason.Boost.key, trans.ratingManipulation.txt()),
      (Reason.Comm.key, trans.troll.txt()),
      (Reason.Other.key, trans.other.txt())
    )

  def translatedModeChoices(using lang: Lang) =
    List(
      (Mode.Casual.id.toString, trans.casual.txt(), none),
      (Mode.Rated.id.toString, trans.rated.txt(), none)
    )

  def translatedIncrementChoices(using lang: Lang) =
    List(
      (1, trans.yes.txt(), none),
      (0, trans.no.txt(), none)
    )

  def translatedModeChoicesTournament(using lang: Lang) =
    List(
      (Mode.Casual.id.toString, trans.casualTournament.txt(), none),
      (Mode.Rated.id.toString, trans.ratedTournament.txt(), none)
    )

  private val encodeId = (v: Variant) => v.id.toString

  private def variantTupleId = variantTuple(encodeId)

  private def variantTuple(encode: Variant => String)(variant: Variant) =
    (encode(variant), variant.name, variant.title.some)

  def translatedVariantChoices(using lang: Lang): List[SelectChoice] =
    translatedVariantChoices(encodeId)

  def translatedVariantChoices(encode: Variant => String)(using lang: Lang): List[SelectChoice] =
    List(
      (encode(chess.variant.Standard), trans.standard.txt(), chess.variant.Standard.title.some)
    )

  def translatedVariantChoicesWithVariants(using lang: Lang): List[SelectChoice] =
    translatedVariantChoicesWithVariants(encodeId)

  def translatedVariantChoicesWithVariants(
      encode: Variant => String
  )(using lang: Lang): List[SelectChoice] =
    translatedVariantChoices(encode) ::: List(
      chess.variant.Crazyhouse,
      chess.variant.Chess960,
      chess.variant.KingOfTheHill,
      chess.variant.ThreeCheck,
      chess.variant.Antichess,
      chess.variant.Atomic,
      chess.variant.Horde,
      chess.variant.RacingKings
    ).map(variantTuple(encode))

  def translatedVariantChoicesWithFen(using lang: Lang) =
    translatedVariantChoices :+
      variantTupleId(chess.variant.Chess960) :+
      variantTupleId(chess.variant.FromPosition)

  def translatedAiVariantChoices(using lang: Lang) =
    translatedVariantChoices :+
      variantTupleId(chess.variant.Crazyhouse) :+
      variantTupleId(chess.variant.Chess960) :+
      variantTupleId(chess.variant.KingOfTheHill) :+
      variantTupleId(chess.variant.ThreeCheck) :+
      variantTupleId(chess.variant.Antichess) :+
      variantTupleId(chess.variant.Atomic) :+
      variantTupleId(chess.variant.Horde) :+
      variantTupleId(chess.variant.RacingKings) :+
      variantTupleId(chess.variant.FromPosition)

  def translatedVariantChoicesWithVariantsAndFen(using lang: Lang) =
    translatedVariantChoicesWithVariants :+
      variantTupleId(chess.variant.FromPosition)

  def translatedSpeedChoices(using lang: Lang) =
    Speed.limited map { s =>
      val minutes = s.range.max / 60 + 1
      (
        s.id.toString,
        s.toString + " - " + trans.lessThanNbMinutes.pluralSameTxt(minutes),
        none
      )
    }

  def translatedSideChoices(using lang: Lang) =
    List(
      ("black", trans.black.txt(), none),
      ("random", trans.randomColor.txt(), none),
      ("white", trans.white.txt(), none)
    )

  def translatedAnimationChoices(using lang: Lang) =
    List(
      (Pref.Animation.NONE, trans.none.txt()),
      (Pref.Animation.FAST, trans.fast.txt()),
      (Pref.Animation.NORMAL, trans.normal.txt()),
      (Pref.Animation.SLOW, trans.slow.txt())
    )

  def translatedBoardCoordinateChoices(using lang: Lang) =
    List(
      (Pref.Coords.NONE, trans.no.txt()),
      (Pref.Coords.INSIDE, trans.insideTheBoard.txt()),
      (Pref.Coords.OUTSIDE, trans.outsideTheBoard.txt())
    )

  def translatedMoveListWhilePlayingChoices(using lang: Lang) =
    List(
      (Pref.Replay.NEVER, trans.never.txt()),
      (Pref.Replay.SLOW, trans.onSlowGames.txt()),
      (Pref.Replay.ALWAYS, trans.always.txt())
    )

  def translatedPieceNotationChoices(using lang: Lang) =
    List(
      (Pref.PieceNotation.SYMBOL, trans.preferences.chessPieceSymbol.txt()),
      (Pref.PieceNotation.LETTER, trans.preferences.pgnLetter.txt())
    )

  def translatedClockTenthsChoices(using lang: Lang) =
    List(
      (Pref.ClockTenths.NEVER, trans.never.txt()),
      (Pref.ClockTenths.LOWTIME, trans.preferences.whenTimeRemainingLessThanTenSeconds.txt()),
      (Pref.ClockTenths.ALWAYS, trans.always.txt())
    )

  def translatedMoveEventChoices(using lang: Lang) =
    List(
      (Pref.MoveEvent.CLICK, trans.preferences.clickTwoSquares.txt()),
      (Pref.MoveEvent.DRAG, trans.preferences.dragPiece.txt()),
      (Pref.MoveEvent.BOTH, trans.preferences.bothClicksAndDrag.txt())
    )

  def translatedTakebackChoices(using lang: Lang) =
    List(
      (Pref.Takeback.NEVER, trans.never.txt()),
      (Pref.Takeback.ALWAYS, trans.always.txt()),
      (Pref.Takeback.CASUAL, trans.preferences.inCasualGamesOnly.txt())
    )

  def translatedMoretimeChoices(using lang: Lang) =
    List(
      (Pref.Moretime.NEVER, trans.never.txt()),
      (Pref.Moretime.ALWAYS, trans.always.txt()),
      (Pref.Moretime.CASUAL, trans.preferences.inCasualGamesOnly.txt())
    )

  def translatedAutoQueenChoices(using lang: Lang) =
    List(
      (Pref.AutoQueen.NEVER, trans.never.txt()),
      (Pref.AutoQueen.PREMOVE, trans.preferences.whenPremoving.txt()),
      (Pref.AutoQueen.ALWAYS, trans.always.txt())
    )

  def translatedAutoThreefoldChoices(using lang: Lang) =
    List(
      (Pref.AutoThreefold.NEVER, trans.never.txt()),
      (Pref.AutoThreefold.ALWAYS, trans.always.txt()),
      (Pref.AutoThreefold.TIME, trans.preferences.whenTimeRemainingLessThanThirtySeconds.txt())
    )

  def submitMoveChoices(using lang: Lang) =
    List(
      (Pref.SubmitMove.NEVER, trans.never.txt()),
      (Pref.SubmitMove.CORRESPONDENCE_ONLY, trans.preferences.inCorrespondenceGames.txt()),
      (Pref.SubmitMove.CORRESPONDENCE_UNLIMITED, trans.preferences.correspondenceAndUnlimited.txt()),
      (Pref.SubmitMove.ALWAYS, trans.always.txt())
    )

  def confirmResignChoices(using lang: Lang) =
    List(
      (Pref.ConfirmResign.NO, trans.no.txt()),
      (Pref.ConfirmResign.YES, trans.yes.txt())
    )

  def translatedRookCastleChoices(using lang: Lang) =
    List(
      (Pref.RookCastle.NO, trans.preferences.castleByMovingTwoSquares.txt()),
      (Pref.RookCastle.YES, trans.preferences.castleByMovingOntoTheRook.txt())
    )

  def translatedChallengeChoices(using lang: Lang) =
    List(
      (Pref.Challenge.NEVER, trans.never.txt()),
      (
        Pref.Challenge.RATING,
        trans.ifRatingIsPlusMinusX.txt(lila.pref.Pref.Challenge.ratingThreshold)
      ),
      (Pref.Challenge.FRIEND, trans.onlyFriends.txt()),
      (Pref.Challenge.REGISTERED, trans.ifRegistered.txt()),
      (Pref.Challenge.ALWAYS, trans.always.txt())
    )

  def translatedMessageChoices(using lang: Lang) =
    List(
      (Pref.Message.NEVER, trans.onlyExistingConversations.txt()),
      (Pref.Message.FRIEND, trans.onlyFriends.txt()),
      (Pref.Message.ALWAYS, trans.always.txt())
    )

  def translatedStudyInviteChoices(using lang: Lang) = privacyBaseChoices
  def translatedPalantirChoices(using lang: Lang)    = privacyBaseChoices
  private def privacyBaseChoices(using lang: Lang) =
    List(
      (Pref.StudyInvite.NEVER, trans.never.txt()),
      (Pref.StudyInvite.FRIEND, trans.onlyFriends.txt()),
      (Pref.StudyInvite.ALWAYS, trans.always.txt())
    )

  def translatedInsightShareChoices(using lang: Lang) =
    List(
      (Pref.InsightShare.NOBODY, trans.withNobody.txt()),
      (Pref.InsightShare.FRIENDS, trans.withFriends.txt()),
      (Pref.InsightShare.EVERYBODY, trans.withEverybody.txt())
    )

  def translatedBoardResizeHandleChoices(using lang: Lang) =
    List(
      (Pref.ResizeHandle.NEVER, trans.never.txt()),
      (Pref.ResizeHandle.INITIAL, trans.preferences.onlyOnInitialPosition.txt()),
      (Pref.ResizeHandle.ALWAYS, trans.always.txt())
    )

  def translatedBlindfoldChoices(using lang: Lang) =
    List(
      Pref.Blindfold.NO  -> trans.no.txt(),
      Pref.Blindfold.YES -> trans.yes.txt()
    )
}
