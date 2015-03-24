import org.scalajs.dom.{Document, _}
import org.scalajs.dom.ext.PimpedNodeList
import org.scalajs.dom.html.Image
import org.scalajs.dom.raw.Node

object Common {

  def transformField(f: (Node, Int, Int) => Any) = for {
    (tr, i) <- document.getElementsByTagName("tr").zipWithIndex
    (td, j) <- tr.asInstanceOf[Document].getElementsByTagName("td").zipWithIndex
  } yield f(td, i, j)

  def visualize(elem: Node, sign: Char, size: Int) = {
    val pic = document.createElement("img").asInstanceOf[Image]
    pic.height = size
    pic.width = size

    pic.src = sign match {
      case 'x' => "images/x.png"
      case 'X' => "images/x1.png"
      case 'o' => "images/o.png"
      case 'O' => "images/o1.png"
      case _ => ""
    }

    if (elem.firstChild != null) {
      elem.replaceChild(pic, elem.firstChild)
    } else {
      elem.appendChild(pic)
    }
  }

}
