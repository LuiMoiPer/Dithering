# Description
My experiments with implementing dithered and quantized images.
I had seen many dithering effects throughout the years and wanted to learn how it was done.

## References
* For error diffusion based dithering, [this video](https://www.youtube.com/watch?v=0L2n8Tg2FwI) by Daniel Shiffman of The Coding Train was my main reference.
* For noise and ordered dithering I referenced [this article](https://surma.dev/things/ditherpunk/) by Surma as well as [an article](https://www.makeworld.space/2021/02/dithering.html) by makeworld.

## Outputs
input image by Akshay Anand
![input image by Akshay Anand](Image/pexels-akshay-anand-3370381.jpg)

The following outputs were done using the [pollen8](https://lospec.com/palette-list/pollen8) from lospec.

Method | Output
:-: | :-:
Quantize | ![quantized image](Image/quantizedOutput1.png)
Random Noise | ![noise dithered image](Image/noiseDitheredOutput1.png)
Ordered | ![ordered dithered image](Image/orderedDitheredOutput1.png)
Error Diffused | ![error difused image](Image/errorDiffusionDitheredOutput1.png)