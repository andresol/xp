package com.enonic.xp.node;

import com.google.common.annotations.Beta;

import com.enonic.xp.aggregation.Aggregations;

@Beta
public class FindNodesByQueryResult
{
    private final NodeHits nodeHits;

    private final Aggregations aggregations;

    private final long totalHits;

    private final long hits;

    private FindNodesByQueryResult( final Builder builder )
    {
        this.nodeHits = builder.nodeHits.build();
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.aggregations = builder.aggregations;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeIds getNodeIds()
    {
        return this.nodeHits.getNodeIds();
    }

    public NodeHits getNodeHits()
    {
        return nodeHits;
    }

    public Aggregations getAggregations()
    {
        return aggregations;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
    }

    public static final class Builder
    {
        private final NodeHits.Builder nodeHits = NodeHits.create();

        private long totalHits;

        private long hits;

        private Aggregations aggregations;

        private Builder()
        {
        }

        public Builder aggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public Builder addNodeHit( final NodeHit nodeHit )
        {
            this.nodeHits.add( nodeHit );
            return this;
        }

        public Builder totalHits( long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public Builder hits( long hits )
        {
            this.hits = hits;
            return this;
        }

        public FindNodesByQueryResult build()
        {
            return new FindNodesByQueryResult( this );
        }
    }
}
