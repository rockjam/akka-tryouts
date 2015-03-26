import org.scalajs.dom.{Document, _}
import org.scalajs.dom.ext.PimpedNodeList
import org.scalajs.dom.html.Image
import org.scalajs.dom.raw.Node

object Common {

  def transformField(f: (Node, Int, Int) => Any):Seq[Any] = transformField(document.asInstanceOf[Element], f)

  def transformField(el:Element, f: (Node, Int, Int) => Any):Seq[Any] = for {
    (tr, i) <- el.getElementsByTagName("tr").zipWithIndex
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

  def createField(size:Int) = {
    val table = document.createElement("table")
    1 to size foreach {e =>
      val tr = document.createElement("tr")
      table.appendChild(tr)
      1 to size foreach {e =>
        tr.appendChild(document.createElement("td"))
      }
    }
    table
  }

}
