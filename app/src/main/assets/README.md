# Sherpa-ONNX Keyword Spotting Assets Directory

To enable offline wake word detection for **"Get Ready Cipher"**, place your ONNX keyword spotting model files inside this directory structure:

## Target Path:
`app/src/main/assets/sherpa-onnx-kws/`

## Required Files:
1. `encoder.onnx` — Transducer encoder model
2. `decoder.onnx` — Transducer decoder model
3. `joiner.onnx` — Transducer joiner model
4. `tokens.txt` — Character/BPE token list
5. `keywords.txt` — Keyword definition file containing:
   ```text
   G ER1 T R EH1 D IY0 S AY1 F ER0 @Get Ready Cipher
   ```

## Download Pre-trained Models:
Download from official Sherpa-ONNX releases:
https://github.com/k2-fsa/sherpa-onnx/releases/tag/kws-models

If these asset files are missing at runtime, Cipher will log a fallback message without crashing and wait for direct manual activation.
