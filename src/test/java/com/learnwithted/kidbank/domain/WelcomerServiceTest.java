package com.learnwithted.kidbank.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WelcomerServiceTest {

  @Test
  public void messageForParentIncludesAllParentCommands() throws Exception {
    WelcomerService welcomerService = new WelcomerService(null, null);

    UserProfile userProfile = new UserProfile("The Parent", null, null, Role.PARENT);
    String message = welcomerService.welcomeMessageFor(userProfile);

    assertThat(message)
        .isEqualTo("Hi The Parent, welcome to Kid Money Manager. You can use the commands: BALANCE, DEPOSIT, SPEND, and GOALS.");
  }

  @Test
  public void messageForKidOnlyIncludesKidCommands() throws Exception {
    WelcomerService welcomerService = new WelcomerService(null, null);

    UserProfile userProfile = new UserProfile("The Kid", null, null, Role.KID);
    String message = welcomerService.welcomeMessageFor(userProfile);

    assertThat(message)
        .isEqualTo("Hi The Kid, welcome to Kid Money Manager. You can use the commands: BALANCE and GOALS.");
  }

    @Test
      public void messageForUnknownUser() throws Exception {
        WelcomerService welcomerService = new WelcomerService(null, null);

        UserProfile userProfile = new UserProfile("User That Does Not Exist", null, null, null);
        // let's say this shouldn't throw an exception if the user does not exist. (?)
        welcomerService.welcome(1234L);
    }

}
