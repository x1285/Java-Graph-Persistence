package de.x1285.jpg.test.model;

import de.x1285.jgp.api.annotation.Property;
import de.x1285.jgp.element.GraphVertex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Software extends GraphVertex {

    @Property
    private String name;

    @Property(label = "language")
    private Language lang;
}
