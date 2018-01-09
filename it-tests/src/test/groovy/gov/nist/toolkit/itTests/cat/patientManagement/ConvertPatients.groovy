package gov.nist.toolkit.itTests.cat.patientManagement

import org.apache.commons.csv.CSVParser
import spock.lang.Specification
import static org.apache.commons.csv.CSVFormat.DEFAULT
/**
 * This takes a spreadsheet from Lynn (csv) and generates the pids format needed for the
 * patient manager in toolkit (https://github.com/usnistgov/iheos-toolkit2/wiki/Patient-ID-Management).
 */
class ConvertPatients extends Specification {
    CSVParser parser

    def setup() {
        String input = this.class.getResource('/testdata/patientManagement/2018.txt').text
        assert input
        parser = CSVParser.parse(input, DEFAULT.withHeader())
    }

    def 'test'() {
        when:
        def items = []
        parser.iterator().each { record ->
            def item = ([
                    blueId : record.'Id in IHEBLUE domain',
                    ihefacility: record.'Id in IHEFACILITY domain',
                    greenId : record.'Id in IHEGREEN domain',
                    redId : record.'Id in IHERED domain',
                    last : record.'Last name',
                    first : record.'First name'
            ])
            if (!item.redId.startsWith('911'))
                items << item
        }
        println items[1]

        StringBuilder buf = new StringBuilder()

        buf.append('[')
        items.each {
            buf.append(noPI(it.redId)).append(',')
            .append(noPI(it.greenId)).append(',')
            .append(noPI(it.blueId)).append(', ')
            .append(it.first).append(' ').append(it.last)
            .append('\n')
        }
        buf.append(']')
        println buf.toString()

        new File('/Users/bill/tmp/toolkit2a/environment/default/pids.txt').text = buf.toString()

        then:
        items.size() > 6
    }

    def noPI(String pid) {
        if (pid.endsWith('^PI'))
            return pid - '^PI'
        return pid
    }
}
