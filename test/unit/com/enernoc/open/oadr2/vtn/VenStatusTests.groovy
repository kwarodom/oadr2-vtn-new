package com.enernoc.open.oadr2.vtn



import grails.test.mixin.*
import org.junit.*

/**
 * Unit test for venstatus
 * @author Yang Xiang
 * 
 */
@TestFor(VenStatus)
@Mock([Ven, Event, Program, VenStatus])
class VenStatusTests {
    
    /**
     * Initial setup for venstatus tests. Adds data into a mock database
     */
    void setUp() {
        mockDomain(Program, [
            [name:"Program1", marketContext:"http://URI1.com"] ])
        
        mockDomain(Event, [
            [program: Program.findWhere(name: "Program1"), eventID: "event1", startDate: new Date(), endDate: new Date().next()] ])
       
        mockDomain(Ven, [
            [programs: Program.findWhere(name: "Program1"), name: "ven-one", venID: "VEN1", clientURI: "http://URI1.com"] ])
    }
    
    /**
     * Test venstatus with no input parameters
     */
    void testNullVenStatus() {
        def nullVenStatus = new VenStatus()
        assert !nullVenStatus.validate()
        assert "nullable" == nullVenStatus.errors["event"].code
        assert "nullable" == nullVenStatus.errors["ven"].code
        assert "nullable" == nullVenStatus.errors["optStatus"].code
        assert "nullable" == nullVenStatus.errors["time"].code
    }
    
    /**
     * Test venstatus toString
     */
    void testToString() {
        def venStatus = new VenStatus(
            event: Event.findWhere(eventID: "event1"),
            ven: Ven.findWhere(venID: "VEN1"), 
            optStatus: StatusCode.OPT_IN,
            time: new Date()
            )
        assert venStatus.validate()
        def toString = "VEN Status \n  VEN ID: $venStatus.ven.venID\n  Event ID: $venStatus.event.eventID\n  Program: $venStatus.event.program.name" +
        "\n  Status: $venStatus.optStatus\n  Time: $venStatus.time"
        assert venStatus.toString() == toString
    }
    
    /**
     * Test venstatus displayTime
     */
    void testDisplayTime() {
        def venStatus = new VenStatus(
            event: Event.findWhere(eventID: "event1"),
            ven: Ven.findWhere(venID: "VEN1"),
            optStatus: StatusCode.OPT_IN,
            time: new Date()
            )
        assert venStatus.validate()
        def displayTime = venStatus.time.format("dd/MM/yyyy HH:mm")
        assert venStatus.displayTime() == displayTime
    }
    
    public String getStatusText() {
        switch(this.optStatus) {
            case(StatusCode.PENDING_DISTRIBUTE) :
                return "Pending Distribute"
            case(StatusCode.DISTRIBUTE_SENT) :
                if (this.event.responseRequired) {
                    return "Awaiting Response"
                } else {
                    return "Payload Sent"
                }
            case(StatusCode.OPT_IN) :
                return "optIn"
            case(StatusCode.OPT_OUT) :
                return "optOut"
            default:
                return null
        }
    }
    
    /**
     * Test venstatus getStatusText() {
     */
    void testGetStatusText() {
        def venStatus = new VenStatus(
            event: Event.findWhere(eventID: "event1"),
            ven: Ven.findWhere(venID: "VEN1"),
            optStatus: StatusCode.PENDING_DISTRIBUTE,
            time: new Date()
            )
        assert venStatus.getStatusText() == "Pending Distribute"
        venStatus.optStatus = StatusCode.DISTRIBUTE_SENT
        assert venStatus.getStatusText() == "Awaiting Response"
        venStatus.optStatus = StatusCode.OPT_IN
        assert venStatus.getStatusText() == "optIn"
        venStatus.optStatus = StatusCode.OPT_OUT
        assert venStatus.getStatusText() == "optOut"
        
    }

}
