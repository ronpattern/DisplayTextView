# DisplayTextView


Custom TextView that show text in animation


## Usage

    <org.altmail.displaytextview.DisplayTextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/message"
        android:layout_gravity="center"
        android:textSize="28sp"
        app:MaxTextSize="80sp"
        app:AutoSizePadding="true"
        app:CharacterAnimatedTogether="2"
        app:MultiLineAnimation="false"/>
       
## Attribute description


**MaxTextSize :** size of characters during animation (default = textSize * 2)

**AutoSizePadding :** automatically calculate the padding so the animation is not partially hidden (default = true)

**CharacterAnimatedTogether :** number of animated characters at the same time (default = 2)

**MultiLineAnimation :** animate the entire paragraph, otherwise line by line (default = false)


## Examples


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
        

![picture alt](https://github.com/ronpattern/DisplayTextView/blob/master/screenshot/screen2.gif)


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
        
        
![picture alt](https://github.com/ronpattern/DisplayTextView/blob/master/screenshot/screen3.gif)


