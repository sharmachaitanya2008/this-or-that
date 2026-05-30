package com.designduel.config;

import com.designduel.model.Design;
import com.designduel.repository.DesignRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final DesignRepository designRepository;

    public DataSeeder(DesignRepository designRepository) {
        this.designRepository = designRepository;
    }

    @Override
    public void run(String... args) {
        if (designRepository.count() > 0) {
            return;
        }

        Design[] designs = {
            Design.builder()
                .title("Hogsmeade Marketplace")
                .description("A charming village marketplace with cobblestone streets, colorful storefronts, and bustling crowds. The design captures the whimsical spirit of a wizarding shopping district with floating lanterns and magical window displays that change seasonally.")
                .imageUrl("https://picsum.photos/seed/hogsmeade/600/400")
                .build(),

            Design.builder()
                .title("Dragon Sanctuary")
                .description("A majestic mountain sanctuary featuring interconnected caves, thermal pools, and soaring flight paths. The layout prioritizes both creature comfort and handler safety with reinforced observation decks and enchanted barrier systems.")
                .imageUrl("https://picsum.photos/seed/dragon/600/400")
                .build(),

            Design.builder()
                .title("Potions Laboratory")
                .description("An alchemist's dream laboratory with crystalline workstations, ingredient archives, and precision brewing chambers. The design emphasizes safety with auto-sealing cabinets and enchanted ventilation while maintaining an atmosphere of arcane discovery.")
                .imageUrl("https://picsum.photos/seed/potions/600/400")
                .build(),

            Design.builder()
                .title("Enchanted Library")
                .description("A vast circular library with towering bookshelves that reach toward an enchanted ceiling mirroring the night sky. Reading nooks float among the stacks, and magical catalogues guide visitors through endless aisles of knowledge.")
                .imageUrl("https://picsum.photos/seed/library/600/400")
                .build(),

            Design.builder()
                .title("Quidditch Arena")
                .description("A state-of-the-art Quidditch stadium with retractable roof, augmented reality scoreboards, and spectator enchantments for perfect viewing from every seat. The pitch features automated goal circles and weather control systems.")
                .imageUrl("https://picsum.photos/seed/quidditch/600/400")
                .build(),

            Design.builder()
                .title("Enchanted Forest Pavilion")
                .description("An open-air pavilion nestled within an ancient enchanted forest. Crystal clearings serve as gathering spaces while bioluminescent flora provides natural lighting. The design harmonizes architecture with living magic.")
                .imageUrl("https://picsum.photos/seed/forest/600/400")
                .build(),

            Design.builder()
                .title("Spellcraft Academy")
                .description("A multi-spire academy designed for advanced spellcraft training. Each tower specializes in a different magical discipline, connected by bridge corridors that offer panoramic views of the surrounding magical landscape.")
                .imageUrl("https://picsum.photos/seed/academy/600/400")
                .build(),

            Design.builder()
                .title("Magical Menagerie")
                .description("An expansive indoor-outdoor habitat facility for magical creatures. Climate-controlled biomes, healing pools, and interactive viewing galleries create an immersive experience while maintaining ethical treatment standards.")
                .imageUrl("https://picsum.photos/seed/menagerie/600/400")
                .build(),

            Design.builder()
                .title("Great Hall Redesign")
                .description("A reimagined great hall featuring modular enchanted ceilings, floating candelabra arrays, and house-themed alcoves. The design accommodates gatherings of all sizes while preserving the awe-inspiring grandeur of traditional architecture.")
                .imageUrl("https://picsum.photos/seed/greathall/600/400")
                .build(),

            Design.builder()
                .title("Wizarding Archives")
                .description("A subterranean archive complex preserving ancient texts and magical artifacts. Climate-controlled vaults, magical preservation fields, and scholar study chambers are organized around a central crystal atrium.")
                .imageUrl("https://picsum.photos/seed/archives/600/400")
                .build(),

            Design.builder()
                .title("Broomstick Workshop")
                .description("An artisan workshop dedicated to broomstick crafting. Open-plan workbenches, material storage for rare woods and bristles, and a testing track winding through the facility showcase the craft of magical transport.")
                .imageUrl("https://picsum.photos/seed/broomshop/600/400")
                .build(),

            Design.builder()
                .title("Astronomy Tower")
                .description("A modernized astronomy tower with an automated telescopic array, projection domes, and celestial mapping chambers. The design incorporates both traditional brass instruments and cutting-edge magical observation technology.")
                .imageUrl("https://picsum.photos/seed/astronomy/600/400")
                .build(),

            Design.builder()
                .title("Greenhouse Complex")
                .description("A series of interconnected greenhouses for magical herbology. Each dome maintains a unique climate, from tropical to arctic, with automated watering systems and magical growth enhancement fields for rare specimens.")
                .imageUrl("https://picsum.photos/seed/greenhouse/600/400")
                .build(),

            Design.builder()
                .title("Wandcraft Emporium")
                .description("An elegant wand shop with towering shelves of wand boxes, a central consultation counter, and a dedicated testing chamber. The warm, intimate space uses magical lighting to highlight the craftsmanship of each wand.")
                .imageUrl("https://picsum.photos/seed/wandshop/600/400")
                .build(),

            Design.builder()
                .title("Merlin's Tower")
                .description("A tribute to the legendary wizard featuring a spiraling tower with artifact displays, holographic historical reenactments, and an interactive spell-learning gallery. The design blends museum reverence with hands-on discovery.")
                .imageUrl("https://picsum.photos/seed/merlin/600/400")
                .build(),

            Design.builder()
                .title("Transfiguration Court")
                .description("A sophisticated training facility for transfiguration studies. Mirror-lined practice rooms, safe transformation zones, and progressive learning chambers allow students to master complex subject magic in controlled environments.")
                .imageUrl("https://picsum.photos/seed/transfiguration/600/400")
                .build(),

            Design.builder()
                .title("Magical Dining Hall")
                .description("An enchanting dining experience with house-elf managed kitchens, floating serving platters, and tables that magically arrange for events. The hall features stained glass windows depicting famous feasts throughout magical history.")
                .imageUrl("https://picsum.photos/seed/dining/600/400")
                .build(),

            Design.builder()
                .title("Defense Against the Dark Arts Center")
                .description("A comprehensive training center with simulation chambers, dueling arenas, and strategic study rooms. Holographic dark creatures provide realistic practice scenarios in a fully controlled safe environment.")
                .imageUrl("https://picsum.photos/seed/defense/600/400")
                .build(),

            Design.builder()
                .title("Herbology Fields")
                .description("Expansive outdoor cultivation fields for magical plants. Raised beds, irrigation canals, and protective enchantments create ideal growing conditions. Research stations allow for close study of rare botanical specimens.")
                .imageUrl("https://picsum.photos/seed/herbology/600/400")
                .build(),

            Design.builder()
                .title("The Leaky Cauldron Inn")
                .description("A cozy wizarding inn featuring themed rooms, a lively pub with magical entertainment, and a sheltered courtyard connecting to the magical shopping district. Warm lighting and rustic furnishings create a welcoming atmosphere.")
                .imageUrl("https://picsum.photos/seed/cauldron/600/400")
                .build()
        };

        for (Design design : designs) {
            designRepository.save(design);
        }

        System.out.println("Seeded " + designs.length + " designs into the database.");
    }
}
