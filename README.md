[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-AndroidOnboarder-green.svg?style=true)](https://android-arsenal.com/details/1/3393)

![Maven Badge](https://maven-badges.herokuapp.com/maven-central/com.github.chyrta/AndroidOnboarder/badge.svg)

# Android Onboarder
Android Onboarder is a lightweight library that helps you create a simple and beautiful welcome screen (as known as App Intro, Onboarding Experience, etc.) for your users.

<image src="https://raw.githubusercontent.com/GrenderG/AndroidOnboarder/master/art/demo1.gif" width="250px">
<image src="https://raw.githubusercontent.com/GrenderG/AndroidOnboarder/master/art/demo2.gif" width="250px">
<image src="https://raw.githubusercontent.com/GrenderG/AndroidOnboarder/master/art/demo3.gif" width="250px">

## Usage

#### Gradle

Add dependency in your build.gradle

```groovy
compile 'com.github.chyrta:AndroidOnboarder:0.6'
```
#### Maven

```
<dependency>
    <groupId>com.github.chyrta</groupId>
    <artifactId>AndroidOnboarder</artifactId>
    <version>0.6</version>
    <type>pom</type>
</dependency>
```

#### Implementation

Create an activity which inherits from OnboarderActivity. Look at the example below to see how the library works.

```java

public class IntroActivity extends OnboarderActivity {

  List<OnboarderPage> onboarderPages;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    onboarderPages = new ArrayList<OnboarderPage>();

    // Create your first page
    OnboarderPage onboarderPage1 = new OnboarderPage("Title 1", "Description 1");

    // You can define the title and description colors (white by default)
    onboarderPage1.setTitleColor(R.color.black);
    onboarderPage1.setDescriptionColor(R.color.white);

    // Set a background color for your page
    onboarderPage1.setBackgroundColor(R.color.my_awesome_color);

    // Create a second page using the default colors
    OnboarderPage onboarderPage2 = new OnboarderPage(R.string.page2_title, R.string.page2_description, R.drawable.my_awesome_image);

    // Add your pages to the list
    onboarderPages.add(onboarderPage1);
    onboarderPages.add(onboarderPage2);

    // And pass your pages to 'setOnboardPagesReady' method
    setOnboardPagesReady(onboarderPages);
  }


  @Override
  protected void onSkipButtonPressed() {
    super.onSkipButtonPressed(); // Optional: by default it skips to the last page

    // Define your actions when the user press 'Skip' button
  }


  @Override
  protected void onFinishButtonPressed() {
    // Define your actions when the user press 'Finish' button
  }

}
```

#### Tips

Remember that you can (Inside your Activity that extends ```OnboarderActivity```):
- Change the colors of the "current page dot" (active and inactive)
- Darken the layout where buttons are placed
- Change divider color, height (in dp) and visibility
- Choose to use ```FloatingActionButton``` or not
- Set text size of title and description
- Change the text on the skip and finish buttons
- Hide the skip button

```java

page.setTitleTextSize(12);
page.setDescriptionTextSize(12);

setActiveIndicatorColor(android.R.color.white);
setInactiveIndicatorColor(android.R.color.darker_gray);
darkenButtonsLayout(true);
setDividerColor(Color.WHITE);
setDividerHeight(2);
setDividerVisibility(View.GONE);
useFloatingActionButton(true);
setSkipButtonTitle("Skip");
setFinishButton("Finish");
hideSkipButton();
```


## Contributions

Feel free to create issues and pull requests

## License

```
MIT License

Copyright (c) 2016 Dzmitry Chyrta, Daniel Morales

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
