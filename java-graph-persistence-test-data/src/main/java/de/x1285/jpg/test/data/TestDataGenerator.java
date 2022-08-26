package de.x1285.jpg.test.data;

import de.x1285.jpg.test.model.Language;
import de.x1285.jpg.test.model.Person;
import de.x1285.jpg.test.model.Place;
import de.x1285.jpg.test.model.Software;

public class TestDataGenerator {

    public static TestData generateTestData() {
        final Person marko = Person.builder().name("Marko").age(29).build();
        final Person josh = Person.builder().name("Josh").age(32).build();
        final Person vadas = Person.builder().name("Vadas").age(27).build();
        final Person peter = Person.builder().name("Peter").age(35).build();
        final Software lop = Software.builder().name("lop").lang(Language.JAVA).build();
        final Software ripple = Software.builder().name("ripple").lang(Language.PYHTON).build();

        marko.knows(josh).setWeight(1.0);
        marko.knows(vadas).setWeight(0.5);
        marko.created(lop).setWeight(0.4);

        josh.created(lop).setWeight(0.4);
        josh.created(ripple).setWeight(1.0);

        peter.created(lop).setWeight(0.2);

        final Place berlin = new Place("Berlin");
        marko.setBirthPlace(berlin);
        josh.setBirthPlace(berlin);

        final Place hamburg = new Place("Hamburg");
        vadas.setBirthPlace(hamburg);

        final Place duesseldorf = new Place("Düsseldorf");
        peter.setBirthPlace(duesseldorf);

        return TestData.builder()
                       .marko(marko)
                       .josh(josh)
                       .vadas(vadas)
                       .peter(peter)
                       .lop(lop)
                       .ripple(ripple)
                       .berlin(berlin)
                       .hamburg(hamburg)
                       .duesseldorf(duesseldorf)
                       .build();
    }

}
