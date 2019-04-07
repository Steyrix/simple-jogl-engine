package engine.core.util.xml

import org.w3c.dom.Document
import java.io.File
import java.lang.IllegalArgumentException
import javax.xml.parsers.DocumentBuilderFactory

class XmlParser {

    companion object {

        private const val EXTENSION = "xml"

        private const val ERROR_MSG_NOT_XML = "The file supplied is not XML!"

        private const val ERROR_MSG_NOT_FOUND = "The file supplied is not found!"

        private const val ERROR_MSG_PARSE_ERR = "Unable to parse xml file"

        fun getDocument(xmlFile: File): Document? {

            if (!xmlFile.exists()) {
                throw IllegalArgumentException(ERROR_MSG_NOT_FOUND)
            }

            if (xmlFile.extension != EXTENSION) {
                throw IllegalArgumentException(ERROR_MSG_NOT_XML)
            }

            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()

            return dBuilder.parse(xmlFile) ?: throw IllegalAccessError(ERROR_MSG_PARSE_ERR)
        }
    }
}