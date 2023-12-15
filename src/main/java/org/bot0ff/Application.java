package org.bot0ff;

import org.bot0ff.entity.Location;
import org.bot0ff.entity.LocationType;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;

@SpringBootApplication
@EnableScheduling
public class Application {
    @Autowired
    LocationRepository locationRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    public CommandLineRunner CommandLineRunnerBean() {
//        return (args) -> {
//            for(int x = 1; x <= Constants.MAX_MAP_LENGTH; x++) {
//                for(int y = 1; y <= Constants.MAX_MAP_LENGTH; y++) {
//                    var location = new Location();
//                    location.setId(Long.valueOf("" + x + y));
//                    location.setName("Равнина");
//                    location.setX(x);
//                    location.setY(y);
//                    location.setLocationType(LocationType.PLAIN);
//                    location.setPlayers(new ArrayList<>());
//                    location.setEnemies(new ArrayList<>());
//                    locationRepository.save(location);
//                }
//            }
//        };
//    }
}
