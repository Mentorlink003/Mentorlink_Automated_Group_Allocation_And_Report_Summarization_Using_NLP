"""
Steps 5-10: LongT5 Summarization.
Uses google/long-t5-tglobal-base for chunk-level and final summarization.
"""

import os
from typing import List, Optional

# Ensure CPU-only
os.environ["CUDA_VISIBLE_DEVICES"] = "-1"

CHUNK_PROMPT = """You are an academic research assistant.

Summarize the following student project report section.

Generate a structured academic summary containing:
Objective
Methodology
Technologies Used
Key Results
Conclusion

Guidelines:
Use formal academic language
Avoid repetition
Focus on core research contributions
Ignore acknowledgements or references

Text:
{chunk}
"""

FINAL_PROMPT = """Generate a final structured academic summary with the following sections:

Objective
Methodology
Technologies Used
Key Results
Conclusion

Ensure clarity and remove redundancy.

The output should be a coherent summary of the entire project report.

Combined summaries:
{combined}
"""

# Generation parameters as specified (beam search for quality)
GEN_PARAMS = {
    "num_beams": 4,
    "max_new_tokens": 250,
    "min_length": 80,
    "no_repeat_ngram_size": 3,
    "length_penalty": 1.2,
    "early_stopping": True,
}


def _load_model():
    """Lazy load model and tokenizer (CPU)."""
    from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
    import torch

    model_name = "google/long-t5-tglobal-base"
    tokenizer = AutoTokenizer.from_pretrained(model_name)
    model = AutoModelForSeq2SeqLM.from_pretrained(model_name)
    model.eval()
    # Force CPU
    device = "cpu"
    model = model.to(device)
    return tokenizer, model, device


_tokenizer = None
_model = None
_device = None


def _get_model():
    global _tokenizer, _model, _device
    if _model is None:
        _tokenizer, _model, _device = _load_model()
    return _tokenizer, _model, _device


def summarize_chunk(chunk: str) -> str:
    """Summarize a single chunk using LongT5."""
    tokenizer, model, device = _get_model()
    prompt = CHUNK_PROMPT.format(chunk=chunk[:8000])  # Truncate if too long
    inputs = tokenizer(prompt, return_tensors="pt", max_length=1024, truncation=True).to(device)
    outputs = model.generate(**inputs, **GEN_PARAMS)
    return tokenizer.decode(outputs[0], skip_special_tokens=True)


def summarize_chunks(chunks: List[str]) -> List[str]:
    """Summarize each chunk and return list of summaries."""
    return [summarize_chunk(c) for c in chunks]


def aggregate_and_final_summarize(chunk_summaries: List[str]) -> str:
    """Combine chunk summaries and run final summarization pass."""
    combined = "\n\n".join(chunk_summaries)
    if len(chunk_summaries) == 1:
        return chunk_summaries[0]
    prompt = FINAL_PROMPT.format(combined=combined[:8000])
    tokenizer, model, device = _get_model()
    inputs = tokenizer(prompt, return_tensors="pt", max_length=1024, truncation=True).to(device)
    outputs = model.generate(**inputs, **GEN_PARAMS)
    return tokenizer.decode(outputs[0], skip_special_tokens=True)


def run_full_summarization(chunks: List[str]) -> str:
    """Run chunk summarization and final aggregation."""
    if not chunks:
        return ""
    chunk_summaries = summarize_chunks(chunks)
    return aggregate_and_final_summarize(chunk_summaries)
