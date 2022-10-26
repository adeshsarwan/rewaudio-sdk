# Rewaudio-sdk
This is an audio ad unit which helps publishers add audio ads in their Aps. Audio has a 20x more value than the banner Ads. This creates a great opportunity for the advertisers to gernerate higher ARPU.
We have developed Rewaudio SDK for Android OS. This SDK can be used by publishers to render audio banners in their applications. [Demo Video](https://drive.google.com/file/d/1KpltJnMTtqgzm8qsgbHYkIY5y5eK0Yb1/view) to see Rewaudio audio ads in action 

## Mediation Layer
Rewaudio SDK has been made as a mediation layer where the first ad request is made to the Rewaudio Ad server for direct ads and only if there are no direct ads the calls are made to Other Ad server for Audio ads.

The Rewaudio SDK and server talks using standard json bid request and bid response. The bid request and bid response are as per openRTB standards. Once the SDK receives the Bid response it takes care of the rendering of image as well as playing of audio. This is natively developed by the Rewaudio team.

## IMA SDK
Rewaudio SDK also has IMA SDK integrated inside it which is used to get indirect audio demand from Google, in case there are no direct ads. IMA SDK will make the call to Google Ad Server, receive the VAST response and take care of the rendering.
Example [Audio VAST Tag URL](https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/audio-preroll&ad_type=audio&sz=1x1&ciu_szs=300x250&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=)

### Settings in GAM server
Step 1: Create a new Ad Unit in GAM
![GAM Create New Ad Unit](https://drive.google.com/file/d/1mzy19kZdxsApFNMCwa1u_AYTzV7CaiuS/view?usp=sharing)

Step 2: From GAM Connect the Ad Unit to the AdX Line Item for Audio demand
