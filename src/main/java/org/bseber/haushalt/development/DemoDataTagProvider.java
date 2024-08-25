package org.bseber.haushalt.development;

import org.bseber.haushalt.tags.NewTag;
import org.bseber.haushalt.tags.TagDescription;
import org.bseber.haushalt.tags.TagName;

import java.util.List;

import static org.bseber.haushalt.tags.TagColor.LIME;

class DemoDataTagProvider {

    List<NewTag> createDemoTags() {
        return List.of(
            new NewTag(new TagName("Fahrrad"), new TagDescription("Alles was mit dem Fahrrad zu tun hat. Verschleißteile, Urlaub, ..."), LIME),
            new NewTag(new TagName("Arbeit"), new TagDescription( "z. B. Dienstreisen"), LIME),
            new NewTag(new TagName("Restaurant"), new TagDescription( "Essen zum Vergnügen oder einfach nur aus der Reihe"), LIME),
            new NewTag(new TagName("Mittagessen"), new TagDescription( "Mittagessen, meistens wohl in der Mittagspause"), LIME),
            new NewTag(new TagName("Nahrungsmittel"), new TagDescription( "Täglicher Bedarf"), LIME),
            new NewTag(new TagName("Kosmetik"), new TagDescription( "Täglicher Bedarf"), LIME)
        );
    }
}
