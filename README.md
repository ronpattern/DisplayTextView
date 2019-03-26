# DisplayTextView


Custom TextView that show text in animation


## Download


Include the following dependency in your build.gradle file :

```java
dependencies {
    ...
    implementation 'org.altmail:display-textview:1.1'
}
```

## Usage

```xml
    <org.altmail.displaytextview.DisplayTextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/my_text_view"
        android:text="@string/message"
        android:layout_gravity="center"
        android:textSize="28sp"
        app:MaxTextSize="80sp"
        app:AutoSizePadding="true"
        app:AnimationDuration="3000"
        app:TextViewInterpolator="linear"
        app:CharacterAnimatedTogether="2"
        app:MultiLineAnimation="false"
        app:hideUntilAnimation="true"/>
```        
        
**In main Activy or Fragment :**  

```java

DisplayTextView myTextView = (DisplayTextView) findViewById(R.id.my_text_view);
...
myTextView.startAnimation();


```
       
### Attribute description


**MaxTextSize :** size of characters during animation (default = textSize * 2)

**AutoSizePadding :** automatically calculate the padding so the animation is not partially hidden (default = true)

**CharacterAnimatedTogether :** number of animated characters at the same time (default = 2)

**MultiLineAnimation :** animate the entire paragraph, otherwise line by line, if true the AnimationDuration is not respected (default = false)

**TextViewInterpolator :** animation interpolator (default = linear)

**hideUntilAnimation :** hide text until animation starts


## Examples

```xml
    <org.altmail.displaytextview.DisplayTextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/message"
        android:layout_gravity="center"
        android:textSize="28sp"
        app:MaxTextSize="80sp"
        app:AutoSizePadding="true"
        app:CharacterAnimatedTogether="3"
        app:MultiLineAnimation="false"/>
```       

![picture alt](https://github.com/ronpattern/DisplayTextView/blob/master/screenshot/screen2.gif)

```xml
    <org.altmail.displaytextview.DisplayTextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/message"
        android:layout_gravity="center"
        android:textSize="28sp"
        app:MaxTextSize="80sp"
        app:AutoSizePadding="true"
        app:CharacterAnimatedTogether="3"
        app:MultiLineAnimation="true"/>
```        
        
![picture alt](https://github.com/ronpattern/DisplayTextView/blob/master/screenshot/screen3.gif)


