package lila.study

case class Position(chapter: Chapter, path: Path):

  def ref = Position.Ref(chapter.id, path)

  def node: Option[RootOrNode] = chapter.root nodeAt path

  override def toString = ref.toString

case object Position:

  case class Ref(chapterId: StudyChapterId, path: Path):

    def encode = s"$chapterId $path"

    def +(node: Node) = copy(path = path + node)

    def withPath(p: Path) = copy(path = p)

  object Ref:

    def decode(str: String): Option[Ref] =
      str.split(' ') match
        case Array(chapterId, path) => Ref(StudyChapterId(chapterId), Path(path)).some
        case Array(chapterId)       => Ref(StudyChapterId(chapterId), Path.root).some
        case _                      => none
